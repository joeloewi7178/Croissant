package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.annotation.StringRes
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.model.Account
import com.joeloewi.croissant.core.model.ResinStatusWidget
import com.joeloewi.croissant.core.data.model.UserInfo
import com.joeloewi.croissant.domain.AccountUseCase
import com.joeloewi.croissant.domain.HoYoLABUseCase
import com.joeloewi.croissant.domain.ResinStatusWidgetUseCase
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateResinStatusWidgetViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val insertResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Insert,
    private val insertAccountUseCase: AccountUseCase.Insert,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    ContainerHost<CreateResinStatusWidgetViewModel.State, CreateResinStatusWidgetViewModel.SideEffect> {

    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.APP_WIDGET_ID

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            savedStateHandle.getStateFlow(_appWidgetIdKey, AppWidgetManager.INVALID_APPWIDGET_ID)
                .collect { reduce { state.copy(appWidgetId = it.toLong()) } }
        }
    }

    fun onReceiveCookie(cookie: String) {
        intent {
            postSideEffect(SideEffect.ShowProgressDialog(R.string.retrieving_data))

            try {
                val userInfo = getUserFullInfoHoYoLABUseCase(cookie).getOrThrow()

                val newState = state.apply {
                    withContext(Dispatchers.Main) {
                        userInfos.add(cookie to userInfo)
                    }
                }

                reduce { newState }
            } catch (cause: CancellationException) {
                throw cause
            } catch (cause: Throwable) {
                postSideEffect(SideEffect.ShowSnackbar)
            } finally {
                postSideEffect(SideEffect.DismissProgressDialog)
            }
        }
    }

    fun setInterval(interval: Long) {
        intent {
            reduce { state.copy(interval = interval) }
        }
    }

    fun configureAppWidget(
        appWidgetId: Int,
        interval: Long,
        userInfos: List<Pair<String, UserInfo>>
    ) {
        intent {
            postSideEffect(SideEffect.ShowProgressDialog())

            runCatching {
                val resinStatusWidget = ResinStatusWidget(
                    appWidgetId = appWidgetId,
                    interval = interval,
                )

                val resinStatusWidgetId = insertResinStatusWidgetUseCase(
                    resinStatusWidget = resinStatusWidget
                )

                val accounts = userInfos
                    .map {
                        Account(
                            resinStatusWidgetId = resinStatusWidgetId,
                            cookie = it.first,
                            uid = it.second.uid
                        )
                    }

                val periodicWorkRequest = RefreshResinStatusWorker.buildPeriodicWork(
                    repeatInterval = interval,
                    repeatIntervalTimeUnit = TimeUnit.MINUTES,
                    appWidgetId = appWidgetId
                )

                workManager.enqueueUniquePeriodicWork(
                    resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    periodicWorkRequest
                ).await()

                insertAccountUseCase(*accounts.toTypedArray())
            }

            postSideEffect(SideEffect.DismissProgressDialog)
            postSideEffect(SideEffect.FinishActivity(appWidgetId))
        }
    }

    data class State(
        val appWidgetId: Long = -1,
        val selectableIntervals: ImmutableList<Long> = persistentListOf(15, 30),
        val interval: Long = 15,
        val userInfo: UserInfo? = null,
        val userInfos: SnapshotStateList<Pair<String, UserInfo>> = SnapshotStateList()
    )

    sealed class SideEffect {
        data class ShowProgressDialog(
            @StringRes val textResourceId: Int? = null
        ) : SideEffect()

        data class FinishActivity(
            val appWidgetId: Int
        ) : SideEffect()

        data object DismissProgressDialog : SideEffect()
        data object ShowSnackbar : SideEffect()
    }
}