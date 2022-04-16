package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Attendance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val application: Application,
    private val croissantDatabase: CroissantDatabase
) : ViewModel() {

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
            runCatching {
                listOf(
                    attendance.checkSessionWorkerName,
                    attendance.attendCheckInEventWorkerName,
                    attendance.oneTimeAttendCheckInEventWorkerName
                ).map { it.toString() }.map { uniqueWorkName ->
                    async {
                        WorkManager.getInstance(application).cancelUniqueWork(uniqueWorkName)
                            .await()
                    }
                }.awaitAll()

                croissantDatabase.attendanceDao().delete(attendance)
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}