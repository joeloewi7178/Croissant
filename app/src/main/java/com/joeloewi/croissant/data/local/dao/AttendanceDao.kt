package com.joeloewi.croissant.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.croissant.data.local.model.Attendance
import com.joeloewi.croissant.data.local.model.AttendanceWithGames

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: Attendance): Long

    @Update
    suspend fun update(vararg attendances: Attendance): Int

    @Delete
    suspend fun delete(vararg attendances: Attendance): Int

    @Transaction
    @Query("SELECT * FROM Attendance WHERE id = :id")
    suspend fun getOne(id: Long): AttendanceWithGames

    @Transaction
    @Query("SELECT * FROM Attendance ORDER BY createdAt DESC")
    fun getAllPaged(): PagingSource<Int, AttendanceWithGames>
}