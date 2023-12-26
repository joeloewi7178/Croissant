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

package com.joeloewi.croissant.data.repository

import androidx.paging.PagingData
import com.joeloewi.croissant.data.repository.local.AttendanceDataSource
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.entity.relational.AttendanceWithGames
import com.joeloewi.croissant.domain.repository.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val attendanceDataSource: AttendanceDataSource,
) : AttendanceRepository {
    override suspend fun insert(attendance: Attendance): Long = withContext(Dispatchers.IO) {
        attendanceDataSource.insert(attendance)
    }

    override suspend fun update(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDataSource.update(*attendances)
    }

    override suspend fun delete(vararg attendances: Attendance): Int = withContext(Dispatchers.IO) {
        attendanceDataSource.delete(*attendances)
    }


    override suspend fun getOneByUid(uid: Long): Attendance = withContext(Dispatchers.IO) {
        attendanceDataSource.getOneByUid(uid)
    }

    override suspend fun getOne(id: Long): AttendanceWithGames =
        withContext(Dispatchers.IO) {
            attendanceDataSource.getOne(id)
        }

    override suspend fun getByIds(vararg ids: Long): List<AttendanceWithGames> =
        withContext(Dispatchers.IO) {
            attendanceDataSource.getByIds(*ids)
        }

    override fun getAllPaged(): Flow<PagingData<AttendanceWithGames>> =
        attendanceDataSource.getAllPaged().flowOn(Dispatchers.IO)

    override suspend fun getAllOneShot(): List<Attendance> = withContext(Dispatchers.IO) {
        attendanceDataSource.getAllOneShot()
    }
}