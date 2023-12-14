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

package com.joeloewi.croissant.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.joeloewi.croissant.data.entity.local.ResinStatusWidgetEntity
import com.joeloewi.croissant.data.entity.local.relational.ResinStatusWidgetWithAccountsEntity

@Dao
interface ResinStatusWidgetDao {

    @Query("SELECT * FROM ResinStatusWidgetEntity")
    suspend fun getAll(): List<ResinStatusWidgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resinStatusWidgetEntity: ResinStatusWidgetEntity): Long

    @Delete
    suspend fun delete(vararg resinStatusWidgetEntities: ResinStatusWidgetEntity): Int

    @Update
    suspend fun update(resinStatusWidgetEntity: ResinStatusWidgetEntity): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidgetEntity WHERE id = :id")
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccountsEntity

    @Transaction
    @Query("DELETE FROM ResinStatusWidgetEntity WHERE appWidgetId IN (:appWidgetIds)")
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidgetEntity WHERE appWidgetId = :appWidgetId")
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccountsEntity
}