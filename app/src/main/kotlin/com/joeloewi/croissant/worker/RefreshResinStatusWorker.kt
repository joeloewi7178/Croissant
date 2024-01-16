package com.joeloewi.croissant.worker

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.PowerManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.entity.DataSwitch
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.util.ResinStatus
import com.joeloewi.croissant.util.createContentRemoteViews
import com.joeloewi.croissant.util.createErrorDueToPowerSaveModeRemoteViews
import com.joeloewi.croissant.util.createLoadingRemoteViews
import com.joeloewi.croissant.util.createUnknownErrorRemoteViews
import com.joeloewi.croissant.util.withBoundNetwork
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        withBoundNetwork {
            runCatching {
                if (powerManager.isInteractive) {
                    //loading view
                    _appWidgetManager.updateAppWidget(
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
                            onFailure = { cause ->
                                if (cause is CancellationException) {
                                    throw cause
                                }
                                ResinStatus()
                            }
                        )
                    }

                    _appWidgetManager.updateAppWidget(
                        _appWidgetId,
                        createContentRemoteViews(context, _appWidgetId, resinStatuses)
                    )
                }
            }.fold(
                onSuccess = {
                    Result.success()
                },
                onFailure = { cause ->
                    if (cause is CancellationException) {
                        throw cause
                    }
                    //hoyoverse api rarely throws timeout error
                    //even though this worker has constraints on connection

                    Firebase.crashlytics.apply {
                        log(this@RefreshResinStatusWorker.javaClass.simpleName)
                        recordException(cause)
                    }

                    val remoteViews = if (powerManager.isPowerSaveMode) {
                        createErrorDueToPowerSaveModeRemoteViews(context, _appWidgetId)
                    } else {
                        //error view
                        createUnknownErrorRemoteViews(context, _appWidgetId)
                    }

                    _appWidgetManager?.updateAppWidget(
                        _appWidgetId,
                        remoteViews
                    )

                    Result.failure()
                }
            )
        }
    }

    companion object {
        const val APP_WIDGET_ID = "appWidgetId"
    }
}