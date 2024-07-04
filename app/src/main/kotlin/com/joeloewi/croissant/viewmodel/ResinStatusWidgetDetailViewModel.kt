package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ResinStatusWidgetDetailViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    private val updateResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Update,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    ContainerHost<ResinStatusWidgetDetailViewModel.State, ResinStatusWidgetDetailViewModel.SideEffect> {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.APP_WIDGET_ID

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            reduce {
                state.copy(
                    appWidgetId = savedStateHandle.get<Int>(_appWidgetIdKey)
                        ?: AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }

            getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                invoke(appWidgetId = state.appWidgetId)
            }.onSuccess {
                reduce { state.copy(interval = it.resinStatusWidget.interval) }
            }
        }
    }

    fun setInterval(interval: Long) = intent {
        reduce { state.copy(interval = interval) }
    }

    fun updateResinStatusWidget() = intent {
        postSideEffect(SideEffect.ShowProgressDialog)
        getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
            invoke(appWidgetId = state.appWidgetId)
        }.mapCatching {
            val resinStatusWidget = it.resinStatusWidget

            val periodicWorkRequest = RefreshResinStatusWorker.buildPeriodicWork(
                repeatInterval = state.interval,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
                appWidgetId = state.appWidgetId
            )

            workManager.enqueueUniquePeriodicWork(
                resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                periodicWorkRequest
            ).await()

            it.resinStatusWidget.copy(
                interval = state.interval
            )
        }.mapCatching {
            updateResinStatusWidgetUseCase(it)
        }
        postSideEffect(SideEffect.DismissProgressDialog)
        postSideEffect(SideEffect.FinishActivity)
    }

    data class State(
        val appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
        val selectableIntervals: ImmutableList<Long> = persistentListOf(15L, 30L),
        val interval: Long = selectableIntervals.first()
    )

    sealed class SideEffect {
        data object ShowProgressDialog : SideEffect()
        data object DismissProgressDialog : SideEffect()
        data object FinishActivity : SideEffect()
    }
}