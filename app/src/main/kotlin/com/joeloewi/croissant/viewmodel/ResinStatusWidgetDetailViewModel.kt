package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ResinStatusWidgetDetailViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    private val updateResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Update,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.APP_WIDGET_ID
    private val _appWidgetId =
        savedStateHandle.get<Int>(_appWidgetIdKey) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val selectableIntervals = listOf(15L, 30L)

    private val _updateResinStatusWidgetState = MutableStateFlow<LCE<Int>>(LCE.Content(0))
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val updateResinStatusWidgetState = _updateResinStatusWidgetState.asStateFlow()
    val interval = _interval.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                invoke(appWidgetId = _appWidgetId)
            }.mapCatching { resinStatusWidgetWithAccounts ->
                _interval.update { resinStatusWidgetWithAccounts.resinStatusWidget.interval }
            }
        }
    }

    fun setInterval(interval: Long) {
        _interval.update { interval }
    }

    fun updateResinStatusWidget() {
        _updateResinStatusWidgetState.update { LCE.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _updateResinStatusWidgetState.update {
                getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                    invoke(appWidgetId = _appWidgetId)
                }.mapCatching {
                    val resinStatusWidget = it.resinStatusWidget

                    val periodicWorkRequest = PeriodicWorkRequest.Builder(
                        RefreshResinStatusWorker::class.java,
                        _interval.value,
                        TimeUnit.MINUTES
                    ).setInputData(
                        workDataOf(RefreshResinStatusWorker.APP_WIDGET_ID to _appWidgetId)
                    ).setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    ).build()

                    workManager.enqueueUniquePeriodicWork(
                        resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                        ExistingPeriodicWorkPolicy.UPDATE,
                        periodicWorkRequest
                    ).await()

                    it.resinStatusWidget.copy(
                        interval = _interval.value
                    )
                }.mapCatching {
                    updateResinStatusWidgetUseCase(it)
                }.fold(
                    onSuccess = {
                        LCE.Content(it)
                    },
                    onFailure = {
                        LCE.Error(it)
                    }
                )
            }
        }
    }
}