package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
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
    private val application: Application,
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    private val updateResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Update,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.APP_WIDGET_ID
    private val _appWidgetId =
        savedStateHandle.get<Int>(_appWidgetIdKey) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val selectableIntervals = listOf(15L, 30L)

    private val _updateResinStatusWidgetState = MutableStateFlow<Lce<Int>>(Lce.Content(0))
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
        _updateResinStatusWidgetState.update { Lce.Loading }
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

                    WorkManager.getInstance(application)
                        .enqueueUniquePeriodicWork(
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
                        Lce.Content(it)
                    },
                    onFailure = {
                        Lce.Error(it)
                    }
                )
            }
        }
    }
}