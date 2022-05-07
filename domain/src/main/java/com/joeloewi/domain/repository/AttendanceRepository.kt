package com.joeloewi.domain.repository

import androidx.paging.PagingData
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun insert(attendance: Attendance): Long
    suspend fun update(vararg attendances: Attendance): Int
    suspend fun delete(vararg attendances: Attendance): Int
    suspend fun getOne(id: Long): AttendanceWithGames
    suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames>
    fun getAllPaged(): Flow<PagingData<AttendanceWithGames>>
}