package com.joeloewi.croissant.receiver

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.work.WorkManager
import androidx.work.await
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.service.RemoteViewsFactoryService
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.DataSwitch
import com.joeloewi.domain.usecase.HoYoLABUseCase
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.domain.wrapper.getOrThrow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

@AndroidEntryPoint
class ResinStatusWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard

    @Inject
    lateinit var getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId

    @Inject
    lateinit var getGenshinDailyNoteHoYoLABUseCase: HoYoLABUseCase.GetGenshinDailyNote

    @Inject
    lateinit var changeDataSwitchHoYoLABUseCase: HoYoLABUseCase.ChangeDataSwitch

    @Inject
    lateinit var deleteByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.DeleteByAppWidgetId

    @Inject
    lateinit var application: Application

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        goAsync(
            onError = {
                FirebaseCrashlytics.getInstance().apply {
                    log(this@ResinStatusWidgetProvider.javaClass.simpleName)
                    recordException(it)
                }
                //error view
                if (context != null && appWidgetManager != null && appWidgetIds != null) {
                    appWidgetIds.map { appWidgetId ->
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
                                        this@ResinStatusWidgetProvider.javaClass
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
                    }
                }
            }
        ) {
            if (context != null && appWidgetManager != null && appWidgetIds != null) {
                appWidgetIds.map { appWidgetId ->
                    withContext(Dispatchers.Default) {
                        async {
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
                                    async {
                                        getGameRecordCardHoYoLABUseCase(
                                            cookie = account.cookie,
                                            uid = account.uid
                                        ).getOrThrow()?.list?.find { gameRecord ->
                                            HoYoLABGame.findByGameId(gameRecord.gameId) == HoYoLABGame.GenshinImpact
                                        }!!.let { gameRecord ->
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

                                            val genshinDailyNote =
                                                getGenshinDailyNoteHoYoLABUseCase(
                                                    cookie = account.cookie,
                                                    server = gameRecord.region,
                                                    roleId = gameRecord.gameRoleId
                                                ).getOrThrow()

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
                                            this@ResinStatusWidgetProvider.javaClass
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
                            }.also { remoteViews ->
                                appWidgetManager.updateAppWidget(
                                    appWidgetId,
                                    remoteViews
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        goAsync(onError = {}) {
            if (context != null && appWidgetIds != null) {
                appWidgetIds.map { appWidgetId ->
                    withContext(Dispatchers.Default) {
                        async {
                            getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                                invoke(appWidgetId)
                            }.mapCatching {
                                WorkManager.getInstance(context)
                                    .cancelUniqueWork(it.resinStatusWidget.refreshGenshinResinStatusWorkerName.toString())
                                    .await()
                            }.fold(
                                onSuccess = {},
                                onFailure = {
                                    it.printStackTrace()
                                }
                            )
                        }
                    }
                }.awaitAll()

                deleteByAppWidgetIdResinStatusWidgetUseCase(*appWidgetIds)
            }
        }
    }
}