package com.joeloewi.croissant.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshResinStatusWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val appWidgetId = inputData.getInt(APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

    override suspend fun doWork(): Result = runCatching {
        Intent().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS,
                intArrayOf(appWidgetId)
            )
        }
    }.mapCatching {
        context.sendBroadcast(it)
    }.fold(
        onSuccess = {
            Result.success()
        },
        onFailure = {
            Result.failure()
        }
    )

    companion object {
        const val APP_WIDGET_ID = "appWidgetId"
    }
}