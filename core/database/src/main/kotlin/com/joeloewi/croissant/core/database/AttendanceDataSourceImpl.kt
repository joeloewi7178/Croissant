/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.core.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames
import com.joeloewi.croissant.data.mapper.AttendanceMapper
import com.joeloewi.croissant.data.mapper.AttendanceWithGamesMapper
import com.joeloewi.croissant.domain.entity.Attendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceDataSourceImpl @Inject constructor(
    private val attendanceDao: com.joeloewi.croissant.core.database.dao.AttendanceDao,
    private val attendanceMapper: AttendanceMapper,
    private val attendanceWithGamesMapper: AttendanceWithGamesMapper,
) : com.joeloewi.croissant.core.database.AttendanceDataSource {

    override suspend fun insert(attendance: Attendance): Long = withContext(Dispatchers.IO) {
        attendanceDao.insert(attendanceMapper.toData(attendance))
    }

    override suspend fun update(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDao.update(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())
    }

    override suspend fun delete(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDao.delete(*attendances.map { attendanceMapper.toData(it) }.toTypedArray())
    }

    override suspend fun getOneByUid(uid: Long): Attendance = withContext(Dispatchers.IO) {
        attendanceMapper.toDomain(attendanceDao.getOneByUid(uid))
    }

    override suspend fun getOne(id: Long): com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames =
        withContext(Dispatchers.IO) {
            attendanceWithGamesMapper.toDomain(attendanceDao.getOne(id))
        }

    override suspend fun getByIds(vararg ids: Long): List<com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames> =
        withContext(Dispatchers.IO) {
            attendanceDao.getByIds(*ids).map { attendanceWithGamesMapper.toDomain(it) }
        }

    override fun getAllPaged(): Flow<PagingData<com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                attendanceDao.getAllPaged()
            }
        ).flow
            .map { pagingData -> pagingData.map { attendanceWithGamesMapper.toDomain(it) } }
            .flowOn(Dispatchers.IO)

    override suspend fun getAllOneShot(): List<Attendance> = withContext(Dispatchers.IO) {
        attendanceDao.getAllOneShot().map { attendanceMapper.toDomain(it) }
    }
}