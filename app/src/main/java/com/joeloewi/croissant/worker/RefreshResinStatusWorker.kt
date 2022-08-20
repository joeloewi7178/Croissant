package com.joeloewi.croissant.worker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.receiver.ResinStatusWidgetProvider
import com.joeloewi.croissant.service.RemoteViewsFactoryService
import com.joeloewi.croissant.util.ListRemoteViewsFactory
import com.joeloewi.croissant.util.ResinStatus
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

@HiltWorker
class RefreshResinStatusWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val powerManager: PowerManager,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    private val getGenshinDailyNoteHoYoLABUseCase: HoYoLABUseCase.GetGenshinDailyNote,
    private val changeDataSwitchHoYoLABUseCase: HoYoLABUseCase.ChangeDataSwitch
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _appWidgetId =
        inputData.getInt(APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    private val _appWidgetManager by lazy { AppWidgetManager.getInstance(context) }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        runCatching {
            if (powerManager.isInteractive) {
                //loading view
                RemoteViews(
                    context.packageName,
                    R.layout.widget_resin_status_loading
                ).also { remoteViews ->
                    _appWidgetManager.updateAppWidget(
                        _appWidgetId,
                        remoteViews
                    )
                }

                val resinStatusWidgetWithAccounts =
                    getOneByAppWidgetIdResinStatusWidgetUseCase(_appWidgetId)

                val resinStatuses =
                    resinStatusWidgetWithAccounts.accounts.map { account ->
                        async(Dispatchers.IO) {
                            getGameRecordCardHoYoLABUseCase.runCatching {
                                invoke(
                                    cookie = account.cookie,
                                    uid = account.uid
                                ).getOrThrow()?.list
                            }.mapCatching { gameRecords ->
                                gameRecords?.find { gameRecord ->
                                    HoYoLABGame.findByGameId(gameRecord.gameId) == HoYoLABGame.GenshinImpact
                                }!!
                            }.mapCatching { gameRecord ->
                                val isDailyNoteEnabled =
                                    gameRecord.dataSwitches.find { it.switchId == DataSwitch.GENSHIN_IMPACT_DAILY_NOTE_SWITCH_ID }?.isPublic

                                if (isDailyNoteEnabled == false) {
                                    changeDataSwitchHoYoLABUseCase(
                                        cookie = account.cookie,
                                        switchId = DataSwitch.GENSHIN_IMPACT_DAILY_NOTE_SWITCH_ID,
                                        isPublic = true,
                                        gameId = gameRecord.gameId,
                                    ).getOrThrow()
                                }

                                val genshinDailyNote = getGenshinDailyNoteHoYoLABUseCase(
                                    cookie = account.cookie,
                                    server = gameRecord.region,
                                    roleId = gameRecord.gameRoleId
                                ).getOrThrow()

                                ResinStatus(
                                    id = account.id,
                                    nickname = gameRecord.nickname,
                                    currentResin = genshinDailyNote?.currentResin
                                        ?: 0,
                                    maxResin = genshinDailyNote?.maxResin
                                        ?: 0
                                )
                            }.fold(
                                onSuccess = {
                                    it
                                },
                                onFailure = {
                                    ResinStatus()
                                }
                            )
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
                            _appWidgetId,
                            Intent(
                                context,
                                ResinStatusWidgetProvider::class.java
                            ).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                                putExtra(
                                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                    intArrayOf(_appWidgetId)
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
                        val serviceIntent = Intent(
                            context,
                            RemoteViewsFactoryService::class.java
                        )

                        val extrasBundle = bundleOf().apply {
                            putParcelableArrayList(
                                ListRemoteViewsFactory.RESIN_STATUSES,
                                ArrayList(resinStatuses)
                            )
                        }

                        serviceIntent.putExtra(ListRemoteViewsFactory.BUNDLE, extrasBundle)

                        setRemoteAdapter(
                            R.id.resin_statuses, serviceIntent
                        )
                    }
                }.let { remoteViews ->
                    _appWidgetManager.updateAppWidget(
                        _appWidgetId,
                        remoteViews
                    )
                }
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

                if (powerManager.isPowerSaveMode) {
                    RemoteViews(
                        context.packageName,
                        R.layout.widget_resin_status_battery_optimization_enabled
                    ).apply {
                        setOnClickPendingIntent(
                            R.id.button_retry,
                            PendingIntent.getBroadcast(
                                context,
                                _appWidgetId,
                                Intent(
                                    context,
                                    ResinStatusWidgetProvider::class.java
                                ).apply {
                                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                                    putExtra(
                                        AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                        intArrayOf(_appWidgetId)
                                    )
                                },
                                pendingIntentFlagUpdateCurrent
                            )
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            setOnClickPendingIntent(
                                R.id.button_change_setting,
                                PendingIntent.getActivity(
                                    context,
                                    _appWidgetId,
                                    Intent(
                                        Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                    ),
                                    pendingIntentFlagUpdateCurrent
                                )
                            )
                        } else {
                            setViewVisibility(R.id.button_change_setting, View.INVISIBLE)
                        }
                    }.also { remoteViews ->
                        _appWidgetManager?.updateAppWidget(
                            _appWidgetId,
                            remoteViews
                        )
                    }
                } else {
                    //error view
                    RemoteViews(
                        context.packageName,
                        R.layout.widget_resin_status_error
                    ).apply {
                        setOnClickPendingIntent(
                            R.id.button_retry,
                            PendingIntent.getBroadcast(
                                context,
                                _appWidgetId,
                                Intent(
                                    context,
                                    ResinStatusWidgetProvider::class.java
                                ).apply {
                                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                                    putExtra(
                                        AppWidgetManager.EXTRA_APPWIDGET_IDS,
                                        intArrayOf(_appWidgetId)
                                    )
                                },
                                pendingIntentFlagUpdateCurrent
                            )
                        )
                    }.also { remoteViews ->
                        _appWidgetManager.updateAppWidget(
                            _appWidgetId,
                            remoteViews
                        )
                    }
                }

                Result.failure()
            }
        )
    }

    companion object {
        const val APP_WIDGET_ID = "appWidgetId"
    }
}