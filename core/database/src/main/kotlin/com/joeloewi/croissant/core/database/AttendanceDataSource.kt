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

import androidx.paging.PagingData
import com.joeloewi.croissant.core.database.model.AttendanceEntity
import com.joeloewi.croissant.core.database.model.relational.AttendanceWithGamesEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceDataSource {
    suspend fun insert(attendance: AttendanceEntity): Long
    suspend fun update(vararg attendances: AttendanceEntity): Int
    suspend fun delete(vararg attendances: AttendanceEntity): Int
    suspend fun getOneByUid(uid: Long): AttendanceEntity
    suspend fun getOne(id: Long): AttendanceWithGamesEntity
    suspend fun getByIds(vararg ids: Long): List<AttendanceWithGamesEntity>
    fun getAllPaged(): Flow<PagingData<AttendanceWithGamesEntity>>
    suspend fun getAllOneShot(): List<AttendanceEntity>
}