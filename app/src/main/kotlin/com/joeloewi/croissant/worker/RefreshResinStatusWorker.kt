package com.joeloewi.croissant.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.PowerManager
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.core.data.model.DataSwitch
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.util.ResinStatus
import com.joeloewi.croissant.util.createContentRemoteViews
import com.joeloewi.croissant.util.createErrorDueToPowerSaveModeRemoteViews
import com.joeloewi.croissant.util.createLoadingRemoteViews
import com.joeloewi.croissant.util.createUnknownErrorRemoteViews
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshResinStatusWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val powerManager: PowerManager,
    private val appWidgetManager: AppWidgetManager,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    private val getGenshinDailyNoteHoYoLABUseCase: HoYoLABUseCase.GetGenshinDailyNote,
    private val changeDataSwitchHoYoLABUseCase: HoYoLABUseCase.ChangeDataSwitch
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _appWidgetId by lazy {
        inputData.getInt(
            APP_WIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Firebase.crashlytics.log(this@RefreshResinStatusWorker.javaClass.simpleName)

        runCatching {
            if (powerManager.isInteractive) {
                //loading view
                appWidgetManager.updateAppWidget(
                    _appWidgetId,
                    createLoadingRemoteViews(context)
                )

                val resinStatusWidgetWithAccounts =
                    getOneByAppWidgetIdResinStatusWidgetUseCase(_appWidgetId)

                val resinStatuses = resinStatusWidgetWithAccounts.accounts.map { account ->
                    getGameRecordCardHoYoLABUseCase.runCatching {
                        invoke(
                            cookie = account.cookie,
                            uid = account.uid
                        ).getOrThrow()
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
                        onFailure = { cause ->
                            if (cause is CancellationException) {
                                throw cause
                            }
                            ResinStatus()
                        }
                    )
                }

                appWidgetManager.updateAppWidget(
                    _appWidgetId,
                    createContentRemoteViews(context, _appWidgetId, resinStatuses)
                )
            }
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { cause ->
                //hoyoverse api rarely throws timeout error
                //even though this worker has constraints on connection

                when (cause) {
                    is CancellationException -> {
                        throw cause
                    }
                }

                Firebase.crashlytics.recordException(cause)

                val remoteViews = if (powerManager.isPowerSaveMode) {
                    createErrorDueToPowerSaveModeRemoteViews(context, _appWidgetId)
                } else {
                    //error view
                    createUnknownErrorRemoteViews(context, _appWidgetId)
                }

                appWidgetManager.updateAppWidget(
                    _appWidgetId,
                    remoteViews
                )

                //let chained works do their jobs
                Result.success()
            }
        )
    }

    companion object {
        const val APP_WIDGET_ID = "appWidgetId"

        fun buildPeriodicWork(
            repeatInterval: Long,
            repeatIntervalTimeUnit: TimeUnit,
            constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build(),
            appWidgetId: Int
        ) = PeriodicWorkRequestBuilder<RefreshResinStatusWorker>(
            repeatInterval,
            repeatIntervalTimeUnit
        )
            .setInputData(workDataOf(APP_WIDGET_ID to appWidgetId))
            .setConstraints(constraints)
            .build()

        fun buildOneTimeWork(
            constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build(),
            appWidgetId: Int
        ) = OneTimeWorkRequestBuilder<RefreshResinStatusWorker>()
            .setInputData(workDataOf(APP_WIDGET_ID to appWidgetId))
            .setConstraints(constraints)
            .build()
    }
}