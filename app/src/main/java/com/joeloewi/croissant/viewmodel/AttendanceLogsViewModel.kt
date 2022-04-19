package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.joeloewi.croissant.data.common.CroissantWorker
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsViewModel @Inject constructor(
    croissantDatabase: CroissantDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsScreen.ATTENDANCE_ID
    private val _croissantWorkerKey = AttendancesDestination.AttendanceLogsScreen.CROISSANT_WORKER
    private val _attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    private val _croissantWorker =
        savedStateHandle.get<CroissantWorker>(_croissantWorkerKey) ?: CroissantWorker.UNKNOWN

    val pagedAttendanceLogs = Pager(
        config = PagingConfig(
            pageSize = 8,
        ),
        pagingSourceFactory = {
            croissantDatabase.workerExecutionLogDao().getAllPaged(
                attendanceId = _attendanceId,
                croissantWorker = _croissantWorker
            )
        }
    ).flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
}