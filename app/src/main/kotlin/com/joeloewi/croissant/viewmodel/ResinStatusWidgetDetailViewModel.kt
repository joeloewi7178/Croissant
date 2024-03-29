package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.foldAsILCE
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _updateResinStatusWidgetState = MutableStateFlow<ILCE<Int>>(ILCE.Idle)
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val updateResinStatusWidgetState = _updateResinStatusWidgetState.asStateFlow()
    val interval = _interval.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                invoke(appWidgetId = _appWidgetId)
            }.mapCatching { resinStatusWidgetWithAccounts ->
                _interval.value = resinStatusWidgetWithAccounts.resinStatusWidget.interval
            }
        }
    }

    fun setInterval(interval: Long) {
        _interval.value = interval
    }

    fun updateResinStatusWidget() {
        _updateResinStatusWidgetState.value = ILCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _updateResinStatusWidgetState.value =
                getOneByAppWidgetIdResinStatusWidgetUseCase.runCatching {
                    invoke(appWidgetId = _appWidgetId)
                }.mapCatching {
                    val resinStatusWidget = it.resinStatusWidget

                    val periodicWorkRequest = RefreshResinStatusWorker.buildPeriodicWork(
                        repeatInterval = _interval.value,
                        repeatIntervalTimeUnit = TimeUnit.MINUTES,
                        appWidgetId = _appWidgetId
                    )

                    workManager.enqueueUniquePeriodicWork(
                        resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        periodicWorkRequest
                    ).await()

                    it.resinStatusWidget.copy(
                        interval = _interval.value
                    )
                }.mapCatching {
                    updateResinStatusWidgetUseCase(it)
                }.foldAsILCE()
        }
    }
}