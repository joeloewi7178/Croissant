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

package com.joeloewi.croissant.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.joeloewi.croissant.core.database.model.AttendanceEntity
import com.joeloewi.croissant.core.database.model.relational.AttendanceWithGamesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendanceEntity: AttendanceEntity): Long

    @Update
    suspend fun update(vararg attendanceEntities: AttendanceEntity): Int

    @Delete
    suspend fun delete(vararg attendanceEntities: AttendanceEntity): Int

    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            WHERE uid = :uid
        """
    )
    suspend fun getOneByUid(uid: Long): AttendanceEntity

    @Transaction
    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            WHERE id = :id
        """
    )
    suspend fun getOne(id: Long): AttendanceWithGamesEntity

    @Transaction
    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            WHERE id IN (:ids)
        """
    )
    suspend fun getByIds(vararg ids: Long): List<AttendanceWithGamesEntity>

    @Transaction
    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            ORDER BY hourOfDay ASC, minute ASC
        """
    )
    fun getAllPaged(): PagingSource<Int, AttendanceWithGamesEntity>

    @Transaction
    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            ORDER BY hourOfDay ASC, minute ASC
        """
    )
    fun getAll(): Flow<List<AttendanceWithGamesEntity>>

    @Transaction
    @Query(
        """
            SELECT * 
            FROM AttendanceEntity 
            ORDER BY hourOfDay ASC, minute ASC
        """
    )
    suspend fun getAllOneShot(): List<AttendanceEntity>
}