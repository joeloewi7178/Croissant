package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import androidx.work.await
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.usecase.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val application: Application,
    getAllPagedAttendanceWithGamesUseCase: AttendanceUseCase.GetAllPaged,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete
) : ViewModel() {

    val pagedAttendanceWithGames = getAllPagedAttendanceWithGamesUseCase().cachedIn(viewModelScope)

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

                deleteAttendanceUseCase(attendance)
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}