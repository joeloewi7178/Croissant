package com.joeloewi.croissant.worker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.receiver.ResinStatusWidgetProvider
import com.joeloewi.croissant.service.RemoteViewsFactoryService
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.DataSwitch
import com.joeloewi.domain.usecase.HoYoLABUseCase
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.domain.wrapper.getOrThrow
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
@HiltWorker
class RefreshResinStatusWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    val getGenshinDailyNoteHoYoLABUseCase: HoYoLABUseCase.GetGenshinDailyNote,
    val changeDataSwitchHoYoLABUseCase: HoYoLABUseCase.ChangeDataSwitch
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val appWidgetId = inputData.getInt(APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    private val appWidgetManager by lazy { AppWidgetManager.getInstance(context) }

    override suspend fun doWork(): Result = runCatching {
        //loading view
        RemoteViews(
            context.packageName,
            R.layout.widget_resin_status_loading
        ).also { remoteViews ->
            appWidgetManager.updateAppWidget(
                appWidgetId,
                remoteViews
            )
        }

        val resinStatusWidgetWithAccounts =
            getOneByAppWidgetIdResinStatusWidgetUseCase(appWidgetId)

        val resinStatuses =
            resinStatusWidgetWithAccounts.accounts.map { account ->
                withContext(Dispatchers.IO) {
                    async {
                        getGameRecordCardHoYoLABUseCase(
                            cookie = account.cookie,
                            uid = account.uid
                        ).getOrThrow()?.list?.find { gameRecord ->
                            HoYoLABGame.findByGameId(gameRecord.gameId) == HoYoLABGame.GenshinImpact
                        }!!.let { gameRecord ->
                            val isDailyNoteEnabled = measureTimedValue {
                                gameRecord.dataSwitches.find { it.switchId == DataSwitch.GENSHIN_IMPACT_DAILY_NOTE_SWITCH_ID }?.isPublic
                            }.also {
                                FirebaseCrashlytics.getInstance().apply {
                                    log(this@RefreshResinStatusWorker.javaClass.simpleName)
                                    setCustomKey("elapsed time for getGameRecordCard", it.duration.inWholeMilliseconds)
                                }
                            }.value

                            if (isDailyNoteEnabled == false) {
                                measureTimedValue {
                                    changeDataSwitchHoYoLABUseCase(
                                        cookie = account.cookie,
                                        switchId = DataSwitch.GENSHIN_IMPACT_DAILY_NOTE_SWITCH_ID,
                                        isPublic = true,
                                        gameId = gameRecord.gameId,
                                    ).getOrThrow()
                                }.also {
                                    FirebaseCrashlytics.getInstance().apply {
                                        log(this@RefreshResinStatusWorker.javaClass.simpleName)
                                        setCustomKey("elapsed time for changeDataSwitch", it.duration.inWholeMilliseconds)
                                    }
                                }.value
                            }

                            val genshinDailyNote =
                                measureTimedValue {
                                    getGenshinDailyNoteHoYoLABUseCase(
                                        cookie = account.cookie,
                                        server = gameRecord.region,
                                        roleId = gameRecord.gameRoleId
                                    ).getOrThrow()
                                }.also {
                                    FirebaseCrashlytics.getInstance().apply {
                                        log(this@RefreshResinStatusWorker.javaClass.simpleName)
                                        setCustomKey("elapsed time for getGenshinDailyNote", it.duration.inWholeMilliseconds)
                                    }
                                }.value

                            RemoteViewsFactoryService.ResinStatus(
                                id = account.id,
                                nickname = gameRecord.nickname,
                                currentResin = genshinDailyNote?.currentResin
                                    ?: 0,
                                maxResin = genshinDailyNote?.maxResin
                                    ?: 0
                            )
                        }
                    }
                }
            }.awaitAll()

        RemoteViews(
            context.packageName,
            R.layout.widget_resin_status
        ).apply {
            //set timestamp
            val dateTimeFormatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

            val localDateTime =
                Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            val readableTimestamp = dateTimeFormatter.format(localDateTime)

            setTextViewText(R.id.widget_timestamp, readableTimestamp)

            //set click listener
            setOnClickPendingIntent(
                R.id.widget_refresh,
                PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    Intent(
                        context,
                        ResinStatusWidgetProvider::class.java
                    ).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_IDS,
                            intArrayOf(appWidgetId)
                        )
                    },
                    pendingIntentFlagUpdateCurrent
                )
            )

            //set resin status
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val items = RemoteViews.RemoteCollectionItems.Builder()
                    .apply {
                        resinStatuses.forEach {
                            addItem(
                                it.id,
                                RemoteViews(
                                    context.packageName,
                                    android.R.layout.two_line_list_item
                                ).apply {
                                    setTextViewText(
                                        android.R.id.text1,
                                        it.nickname
                                    )
                                    setTextViewText(
                                        android.R.id.text2,
                                        "${it.currentResin} / ${it.maxResin}"
                                    )
                                }
                            )
                        }
                    }
                    .setViewTypeCount(1)
                    .setHasStableIds(false)
                    .build()

                setRemoteAdapter(
                    R.id.resin_statuses,
                    items
                )
            } else {
                val serviceIntent =
                    Intent(
                        context,
                        RemoteViewsFactoryService::class.java
                    ).apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                        putParcelableArrayListExtra(
                            RemoteViewsFactoryService.ListRemoteViewsFactory.RESIN_STATUSES,
                            ArrayList(resinStatuses)
                        )
                        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                    }

                setRemoteAdapter(R.id.resin_statuses, serviceIntent)
            }
        }.let { remoteViews ->
            appWidgetManager.updateAppWidget(
                appWidgetId,
                remoteViews
            )
        }
    }.fold(
        onSuccess = {
            Result.success()
        },
        onFailure = {
            //hoyoverse api rarely throws timeout error
            //even though this worker has constraints on connection

            FirebaseCrashlytics.getInstance().apply {
                log(this@RefreshResinStatusWorker.javaClass.simpleName)
                recordException(it)
            }

            //error view
            RemoteViews(
                context.packageName,
                R.layout.widget_resin_status_error
            ).apply {
                setOnClickPendingIntent(
                    R.id.button_retry,
                    PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        Intent(
                            context,
                            ResinStatusWidgetProvider::class.java
                        ).apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                            putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                intArrayOf(appWidgetId)
                            )
                        },
                        pendingIntentFlagUpdateCurrent
                    )
                )
            }.also { remoteViews ->
                appWidgetManager.updateAppWidget(
                    appWidgetId,
                    remoteViews
                )
            }
            Result.failure()
        }
    )

    companion object {
        const val APP_WIDGET_ID = "appWidgetId"
    }
}