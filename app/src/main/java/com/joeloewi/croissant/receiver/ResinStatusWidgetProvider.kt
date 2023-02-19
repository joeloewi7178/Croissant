package com.joeloewi.croissant.receiver

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.work.*
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

@AndroidEntryPoint
class ResinStatusWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId

    @Inject
    lateinit var deleteByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.DeleteByAppWidgetId

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        //this method also called when user put widget on home screen

        goAsync(
            onError = {},
            coroutineContext = Dispatchers.IO
        ) {
            appWidgetIds.map { appWidgetId ->
                async(Dispatchers.IO) {
                    if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizationsCompat(
                            application
                        )
                    ) {
                        RemoteViews(
                            application.packageName,
                            R.layout.widget_resin_status_battery_optimization_enabled
                        ).apply {
                            setOnClickPendingIntent(
                                R.id.button_retry,
                                PendingIntent.getBroadcast(
                                    application,
                                    appWidgetId,
                                    Intent(
                                        application,
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setOnClickPendingIntent(
                                    R.id.button_change_setting,
                                    PendingIntent.getActivity(
                                        application,
                                        appWidgetId,
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
                            appWidgetManager.updateAppWidget(
                                appWidgetId,
                                remoteViews
                            )
                        }
                    } else {
                        getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                            invoke(appWidgetId)
                        }.mapCatching {
                            val oneTimeWorkRequest =
                                OneTimeWorkRequest.Builder(RefreshResinStatusWorker::class.java)
                                    .setInputData(
                                        workDataOf(RefreshResinStatusWorker.APP_WIDGET_ID to appWidgetId)
                                    )
                                    .setConstraints(
                                        Constraints.Builder()
                                            .setRequiredNetworkType(NetworkType.CONNECTED)
                                            .build()
                                    )
                                    .build()

                            WorkManager.getInstance(application).enqueueUniqueWork(
                                it.resinStatusWidget.id.toString(),
                                ExistingWorkPolicy.APPEND_OR_REPLACE,
                                oneTimeWorkRequest
                            )
                        }.onFailure { cause ->
                            if (cause is CancellationException) {
                                throw cause
                            }
                        }
                    }
                }
            }.awaitAll()
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        goAsync(
            onError = {},
            coroutineContext = Dispatchers.IO
        ) {
            appWidgetIds.run {
                map { appWidgetId ->
                    async(Dispatchers.IO) {
                        getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                            invoke(appWidgetId)
                        }.onSuccess {
                            WorkManager.getInstance(application)
                                .cancelUniqueWork(it.resinStatusWidget.refreshGenshinResinStatusWorkerName.toString())

                        }
                    }
                }.awaitAll()

                deleteByAppWidgetIdResinStatusWidgetUseCase(*this)
            }
        }
    }
}