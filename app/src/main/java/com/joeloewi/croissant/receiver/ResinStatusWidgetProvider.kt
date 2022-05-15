package com.joeloewi.croissant.receiver

import android.app.Application
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.*
import com.joeloewi.croissant.util.goAsync
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ResinStatusWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId

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
        //this method also called when user put widget on home screen

        if (context != null) {
            goAsync(onError = {}) {
                appWidgetIds?.map { appWidgetId ->
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