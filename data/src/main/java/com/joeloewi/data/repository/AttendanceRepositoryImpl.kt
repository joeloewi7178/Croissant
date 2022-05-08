package com.joeloewi.data.repository

import androidx.paging.PagingData
import com.joeloewi.data.repository.local.AttendanceDataSource
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import com.joeloewi.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDataSource: AttendanceDataSource,
) : AttendanceRepository {
    override suspend fun insert(attendance: Attendance): Long =
        attendanceDataSource.insert(attendance)

    override suspend fun update(vararg attendances: Attendance): Int =
        attendanceDataSource.update(*attendances)

    override suspend fun delete(vararg attendances: Attendance): Int =
        attendanceDataSource.delete(*attendances)

    override suspend fun getOneByUid(uid: Long): Attendance = attendanceDataSource.getOneByUid(uid)

    override suspend fun getOne(id: Long): AttendanceWithGames =
        attendanceDataSource.getOne(id)

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames> =
        attendanceDataSource.getByIds(*ids)

    override fun getAllPaged(): Flow<PagingData<AttendanceWithGames>> =
        attendanceDataSource.getAllPaged()
}