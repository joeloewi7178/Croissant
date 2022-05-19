package com.joeloewi.croissant.receiver

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.work.*
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizations
import com.joeloewi.croissant.util.isPowerSaveMode
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@ExperimentalTime
@AndroidEntryPoint
class ResinStatusWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId

    @Inject
    lateinit var deleteByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.DeleteByAppWidgetId

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        //this method also called when user put widget on home screen

        if (context != null) {
            goAsync(onError = {}) {
                appWidgetIds?.map { appWidgetId ->
                    async {
                        if (context.isPowerSaveMode() && !context.isIgnoringBatteryOptimizations()) {
                            RemoteViews(
                                context.packageName,
                                R.layout.widget_resin_status_battery_optimization_enabled
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
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    setOnClickPendingIntent(
                                        R.id.button_change_setting,
                                        PendingIntent.getActivity(
                                            context,
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
                                appWidgetManager?.updateAppWidget(
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
                                        ).setConstraints(
                                            Constraints.Builder()
                                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                                .build()
                                        ).build()

                                WorkManager.getInstance(context).enqueueUniqueWork(
                                    it.resinStatusWidget.id.toString(),
                                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                                    oneTimeWorkRequest
                                ).await()
                            }.onFailure {

                            }
                        }
                    }
                }?.awaitAll()
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
                            val resinStatusWithAccounts =
                                getOneByAppWidgetIdResinStatusWidgetUseCase(appWidgetId)

                            WorkManager.getInstance(context)
                                .cancelUniqueWork(resinStatusWithAccounts.resinStatusWidget.refreshGenshinResinStatusWorkerName.toString())
                                .await()
                        }
                    }
                }.awaitAll()

                deleteByAppWidgetIdResinStatusWidgetUseCase(*appWidgetIds)
            }
        }
    }
}