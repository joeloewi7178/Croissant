package com.joeloewi.data.repository.local.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.data.db.dao.AttendanceDao
import com.joeloewi.data.mapper.AttendanceMapper
import com.joeloewi.data.mapper.AttendanceWithGamesMapper
import com.joeloewi.data.repository.local.AttendanceDataSource
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AttendanceDataSourceImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendanceMapper: AttendanceMapper,
    private val attendanceWithGamesMapper: AttendanceWithGamesMapper,
) : AttendanceDataSource {

    override suspend fun insert(attendance: Attendance): Long =
        attendanceDao.insert(attendanceMapper.toData(attendance))

    override suspend fun update(vararg attendances: Attendance): Int =
        attendanceDao.update(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())

    override suspend fun delete(vararg attendances: Attendance): Int =
        attendanceDao.delete(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())

    override suspend fun getOneByUid(uid: Long): Attendance =
        attendanceMapper.toDomain(attendanceDao.getOneByUid(uid))

    override suspend fun getOne(id: Long): AttendanceWithGames =
        attendanceWithGamesMapper.toDomain(attendanceDao.getOne(id))

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames> =
        attendanceDao.getByIds(*ids).map { attendanceWithGamesMapper.toDomain(it) }

    override fun getAllPaged(): Flow<PagingData<AttendanceWithGames>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                attendanceDao.getAllPaged()
            }
        ).flow
            .map { pagingData -> pagingData.map { attendanceWithGamesMapper.toDomain(it) } }

    override suspend fun getAllOneShot(): List<Attendance> =
        attendanceDao.getAllOneShot().map { attendanceMapper.toDomain(it) }
}