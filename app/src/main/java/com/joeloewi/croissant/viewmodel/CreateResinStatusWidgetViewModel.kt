package com.joeloewi.croissant.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.*
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Account
import com.joeloewi.croissant.data.local.model.ResinStatusWidget
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.worker.RefreshResinStatusWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateResinStatusWidgetViewModel @Inject constructor(
    private val application: Application,
    private val croissantDatabase: CroissantDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val appWidgetId =
        savedStateHandle.get<Int>("appWidgetId") ?: AppWidgetManager.INVALID_APPWIDGET_ID
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
    val pagedAttendancesWithGames = Pager(
        config = PagingConfig(
            pageSize = 8,
        ),
        pagingSourceFactory = {
            croissantDatabase.attendanceDao().getAllPaged()
        }
    ).flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)

    fun setInterval(interval: Long) {
        _interval.value = interval
    }

    fun configureAppWidget() {
        _createResinStatusWidgetState.value = Lce.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _createResinStatusWidgetState.value = this.runCatching {
                val resinStatusWidget = ResinStatusWidget(
                    appWidgetId = appWidgetId,
                    interval = _interval.value,
                )

                val resinStatusWidgetId = croissantDatabase.resinStatusWidgetDao().insert(
                    resinStatusWidget = resinStatusWidget
                )

                val accounts = croissantDatabase.attendanceDao()
                    .getByIds(*checkedAttendanceIds.toLongArray())
                    .map {
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

                croissantDatabase.accountDao().insert(*accounts.toTypedArray())
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