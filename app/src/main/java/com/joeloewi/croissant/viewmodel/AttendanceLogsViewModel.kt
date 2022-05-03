package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.joeloewi.croissant.data.common.LoggableWorker
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsViewModel @Inject constructor(
    private val croissantDatabase: CroissantDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsScreen.ATTENDANCE_ID
    private val _loggableWorkerKey = AttendancesDestination.AttendanceLogsScreen.CROISSANT_WORKER
    private val _attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    private val _loggableWorker =
        savedStateHandle.get<LoggableWorker>(_loggableWorkerKey) ?: LoggableWorker.UNKNOWN

    val pagedAttendanceLogs = Pager(
        config = PagingConfig(
            pageSize = 8,
        ),
        pagingSourceFactory = {
            croissantDatabase.workerExecutionLogDao().getAllPaged(
                attendanceId = _attendanceId,
                loggableWorker = _loggableWorker
            )
        }
    ).flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            croissantDatabase.workerExecutionLogDao().deleteAll(
                attendanceId = _attendanceId,
                loggableWorker = _loggableWorker
            )
        }
    }
}