package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Attendance
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    application: Application,
    private val croissantDatabase: CroissantDatabase
): ViewModel() {

    val pagedAttendanceWithGames = Pager(
        config = PagingConfig(
            pageSize = 8,
        ),
        pagingSourceFactory = {
            croissantDatabase.attendanceDao().getAllPaged()
        }
    ).flow.cachedIn(viewModelScope)

    fun deleteAttendance(attendance: Attendance) {
       viewModelScope.launch(Dispatchers.IO) {
           croissantDatabase.attendanceDao().delete(attendance)
       }
    }
}