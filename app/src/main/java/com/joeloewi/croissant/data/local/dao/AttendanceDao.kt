package com.joeloewi.croissant.data.local.dao

import androidx.room.*
import com.joeloewi.croissant.data.local.model.Attendance
import com.joeloewi.croissant.data.local.model.AttendanceWithAllValues
import com.joeloewi.croissant.data.local.model.AttendanceWithGames

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: Attendance): Long

    @Update
    suspend fun update(vararg attendances: Attendance): Int

    @Delete
    suspend fun delete(vararg attendances: Attendance): Int

    @Query("SELECT * FROM Attendance WHERE id = :id")
    suspend fun getOne(id: Long): AttendanceWithAllValues
}