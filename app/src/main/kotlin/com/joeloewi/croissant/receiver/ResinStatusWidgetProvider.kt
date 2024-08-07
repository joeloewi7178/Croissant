package com.joeloewi.croissant.receiver

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.PowerManager
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.util.createErrorDueToPowerSaveModeRemoteViews
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResinStatusWidgetProvider : AppWidgetProvider() {
    private val _coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        Firebase.crashlytics.apply {
            log(this@ResinStatusWidgetProvider.javaClass.simpleName)
            recordException(throwable)
        }
    }

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId

    @Inject
    lateinit var deleteByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.DeleteByAppWidgetId

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        //this method also called when user put widget on home screen
        coroutineScope.launch(_coroutineContext) {
            appWidgetIds.map { appWidgetId ->
                async(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                    if (powerManager.isPowerSaveMode && !powerManager.isIgnoringBatteryOptimizationsCompat(
                            context
                        )
                    ) {
                        appWidgetManager.updateAppWidget(
                            appWidgetId,
                            createErrorDueToPowerSaveModeRemoteViews(context, appWidgetId)
                        )
                    } else {
                        getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                            invoke(appWidgetId)
                        }.mapCatching {
                            val oneTimeWorkRequest =
                                RefreshResinStatusWorker.buildOneTimeWork(appWidgetId = appWidgetId)

                            workManager.enqueueUniqueWork(
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

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        coroutineScope.launch(_coroutineContext) {
            appWidgetIds.run {
                map { appWidgetId ->
                    async(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                        getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                            invoke(appWidgetId)
                        }.onSuccess {
                            workManager.cancelUniqueWork(it.resinStatusWidget.refreshGenshinResinStatusWorkerName.toString())
                        }
                    }
                }.awaitAll()

                deleteByAppWidgetIdResinStatusWidgetUseCase(*this)
            }
        }

        super.onDeleted(context, appWidgetIds)
    }
}