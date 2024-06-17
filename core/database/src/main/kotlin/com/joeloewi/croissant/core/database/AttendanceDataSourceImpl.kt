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
import com.joeloewi.croissant.core.database.dao.AttendanceDao
import com.joeloewi.croissant.core.database.model.AttendanceEntity
import com.joeloewi.croissant.core.database.model.relational.AttendanceWithGamesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceDataSourceImpl @Inject constructor(
    private val attendanceDao: AttendanceDao,
) : AttendanceDataSource {

    override suspend fun insert(attendance: AttendanceEntity): Long = withContext(Dispatchers.IO) {
        attendanceDao.insert(attendance)
    }

    override suspend fun update(vararg attendances: AttendanceEntity): Int =
        withContext(Dispatchers.IO) {
            attendanceDao.update(*attendances)
        }

    override suspend fun delete(vararg attendances: AttendanceEntity): Int =
        withContext(Dispatchers.IO) {
            attendanceDao.delete(*attendances)
        }

    override suspend fun getOneByUid(uid: Long): AttendanceEntity = withContext(Dispatchers.IO) {
        attendanceDao.getOneByUid(uid)
    }

    override suspend fun getOne(id: Long): AttendanceWithGamesEntity = withContext(Dispatchers.IO) {
        attendanceDao.getOne(id)
    }

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGamesEntity> =
        withContext(Dispatchers.IO) {
            attendanceDao.getByIds(*ids)
        }

    override fun getAllPaged(): Flow<PagingData<AttendanceWithGamesEntity>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                attendanceDao.getAllPaged()
            }
        ).flow.flowOn(Dispatchers.IO)

    override suspend fun getAllOneShot(): List<AttendanceEntity> =
        withContext(Dispatchers.IO) { attendanceDao.getAllOneShot() }
}