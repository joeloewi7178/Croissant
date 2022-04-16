package com.joeloewi.croissant.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.croissant.data.common.CroissantWorker
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.local.model.relational.WorkerExecutionLogWithState

@Dao
interface WorkerExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long

    @Delete
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int

    @Transaction
    @Query("SELECT * FROM WorkerExecutionLog WHERE attendanceId = :attendanceId AND worker = :croissantWorker ORDER BY createdAt DESC")
    fun getAllPaged(
        attendanceId: Long,
        croissantWorker: CroissantWorker
    ): PagingSource<Int, WorkerExecutionLogWithState>
}