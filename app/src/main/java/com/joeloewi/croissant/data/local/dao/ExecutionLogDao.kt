package com.joeloewi.croissant.data.local.dao

import androidx.room.*
import com.joeloewi.croissant.data.local.model.ExecutionLog

@Dao
interface ExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(executionLog: ExecutionLog): Long

    @Update
    suspend fun update(vararg executionLogs: ExecutionLog): Int

    @Delete
    suspend fun delete(vararg executionLogs: ExecutionLog): Int
}