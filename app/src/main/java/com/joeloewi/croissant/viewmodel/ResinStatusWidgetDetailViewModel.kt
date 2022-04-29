package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.state.Lce
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
    private val application: Application,
    private val croissantDatabase: CroissantDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val appWidgetId =
        savedStateHandle.get<Int>("appWidgetId") ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val selectableIntervals = listOf(15L, 30L, 60L)

    private val _updateResinStatusWidgetState = MutableStateFlow<Lce<Int>>(Lce.Content(0))
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val updateResinStatusWidgetState = _updateResinStatusWidgetState.asStateFlow()
    val interval = _interval.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            croissantDatabase.resinStatusWidgetDao().runCatching {
                getOneByAppWidgetId(appWidgetId = appWidgetId)
            }.mapCatching {
                _interval.value = it.resinStatusWidget.interval
            }
        }
    }

    fun setInterval(interval: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _interval.value = interval
        }
    }

    fun updateResinStatusWidget() {
        _updateResinStatusWidgetState.value = Lce.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _updateResinStatusWidgetState.value =
                croissantDatabase.resinStatusWidgetDao().runCatching {
                    getOneByAppWidgetId(appWidgetId = appWidgetId)
                }.mapCatching {
                    val resinStatusWidget = it.resinStatusWidget

                    val periodicWorkRequest = PeriodicWorkRequest.Builder(
                        RefreshResinStatusWorker::class.java,
                        _interval.value,
                        TimeUnit.MINUTES
                    ).setInputData(
                        workDataOf(RefreshResinStatusWorker.APP_WIDGET_ID to appWidgetId)
                    ).setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    ).build()

                    WorkManager.getInstance(application)
                        .enqueueUniquePeriodicWork(
                            resinStatusWidget.refreshGenshinResinStatusWorkerName.toString(),
                            ExistingPeriodicWorkPolicy.REPLACE,
                            periodicWorkRequest
                        ).await()

                    it.resinStatusWidget.copy(
                        interval = _interval.value
                    )
                }.mapCatching {
                    croissantDatabase.resinStatusWidgetDao().update(it)
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