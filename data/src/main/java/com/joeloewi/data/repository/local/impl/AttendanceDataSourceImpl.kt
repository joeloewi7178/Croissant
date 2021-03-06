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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceDataSourceImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val attendanceMapper: AttendanceMapper,
    private val attendanceWithGamesMapper: AttendanceWithGamesMapper,
) : AttendanceDataSource {

    override suspend fun insert(attendance: Attendance): Long =
        withContext(coroutineDispatcher) {
            attendanceDao.insert(attendanceMapper.toData(attendance))
        }

    override suspend fun update(vararg attendances: Attendance): Int =
        withContext(coroutineDispatcher) {
            attendanceDao.update(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())
        }

    override suspend fun delete(vararg attendances: Attendance): Int =
        withContext(coroutineDispatcher) {
            attendanceDao.delete(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())
        }

    override suspend fun getOneByUid(uid: Long): Attendance = withContext(coroutineDispatcher) {
        attendanceMapper.toDomain(attendanceDao.getOneByUid(uid))
    }

    override suspend fun getOne(id: Long): AttendanceWithGames =
        withContext(coroutineDispatcher) {
            attendanceWithGamesMapper.toDomain(attendanceDao.getOne(id))
        }

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames> =
        withContext(coroutineDispatcher) {
            attendanceDao.getByIds(*ids).map { attendanceWithGamesMapper.toDomain(it) }
        }

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
            .flowOn(coroutineDispatcher)

    override suspend fun getAllOneShot(): List<Attendance> =
        withContext(coroutineDispatcher) {
            attendanceDao.getAllOneShot().map { attendanceMapper.toDomain(it) }
        }
}