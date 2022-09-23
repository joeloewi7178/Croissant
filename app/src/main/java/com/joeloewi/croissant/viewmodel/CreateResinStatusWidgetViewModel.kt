package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.*
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import com.joeloewi.domain.entity.Account
import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.usecase.AccountUseCase
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateResinStatusWidgetViewModel @Inject constructor(
    private val application: Application,
    getAllPagedAttendanceUseCase: AttendanceUseCase.GetAllPaged,
    private val insertResinStatusWidgetUseCase: ResinStatusWidgetUseCase.Insert,
    private val getByIdsAttendanceUseCase: AttendanceUseCase.GetByIds,
    private val insertAccountUseCase: AccountUseCase.Insert,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.APP_WIDGET_ID
    val appWidgetId =
        savedStateHandle.get<Int>(_appWidgetIdKey) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    val selectableIntervals = listOf(15L, 30L, 60L)

    private val _createResinStatusWidgetState = MutableStateFlow<Lce<List<Long>>>(
        Lce.Content(
            listOf()
        )
    )
    private val _interval = MutableStateFlow(selectableIntervals.first())

    val createResinStatusWidgetState = _createResinStatusWidgetState.asStateFlow()
    val interval = _interval.asStateFlow()

    //these are connected to genshin impact
    val checkedAttendanceIds = SnapshotStateList<Long>()
    val pagedAttendancesWithGames = getAllPagedAttendanceUseCase().cachedIn(viewModelScope)

    fun setInterval(interval: Long) {
        _interval.update { interval }
    }

    fun configureAppWidget() {
        _createResinStatusWidgetState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _createResinStatusWidgetState.update {
                this.runCatching {
                    val resinStatusWidget = ResinStatusWidget(
                        appWidgetId = appWidgetId,
                        interval = _interval.value,
                    )

                    val resinStatusWidgetId = insertResinStatusWidgetUseCase(
                        resinStatusWidget = resinStatusWidget
                    )

                    val accounts = getByIdsAttendanceUseCase(
                        *checkedAttendanceIds.toLongArray()
                    ).map {
                        Account(
                            resinStatusWidgetId = resinStatusWidgetId,
                            cookie = it.attendance.cookie,
                            uid = it.attendance.uid
                        )
                    }

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

                    insertAccountUseCase(*accounts.toTypedArray())
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