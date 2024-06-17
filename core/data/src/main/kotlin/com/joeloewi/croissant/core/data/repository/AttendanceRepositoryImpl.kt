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

package com.joeloewi.croissant.core.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.core.data.model.asData
import com.joeloewi.croissant.core.data.model.asExternalData
import com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames
import com.joeloewi.croissant.core.data.model.relational.asExternalData
import com.joeloewi.croissant.core.database.AttendanceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDataSource: AttendanceDataSource,
) : AttendanceRepository {
    override suspend fun insert(attendance: Attendance): Long = withContext(Dispatchers.IO) {
        attendanceDataSource.insert(attendance.asData())
    }

    override suspend fun update(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDataSource.update(*attendances.map { it.asData() }.toTypedArray())
    }

    override suspend fun delete(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDataSource.delete(*attendances.map { it.asData() }.toTypedArray())
    }

    override suspend fun getOneByUid(uid: Long): Attendance = withContext(Dispatchers.IO) {
        attendanceDataSource.getOneByUid(uid).asExternalData()
    }

    override suspend fun getOne(id: Long): AttendanceWithGames = withContext(Dispatchers.IO) {
        attendanceDataSource.getOne(id).asExternalData()
    }

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames> =
        withContext(Dispatchers.IO) {
            attendanceDataSource.getByIds(*ids).map { it.asExternalData() }
        }

    override fun getAllPaged(): Flow<PagingData<AttendanceWithGames>> =
        attendanceDataSource.getAllPaged()
            .map { pagingData -> pagingData.map { it.asExternalData() } }.flowOn(Dispatchers.IO)

    override suspend fun getAllOneShot(): List<Attendance> = withContext(Dispatchers.IO) {
        attendanceDataSource.getAllOneShot().map { it.asExternalData() }
    }
}