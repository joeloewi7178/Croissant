package com.joeloewi.croissant.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.croissant.data.local.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM Reminder")
    suspend fun getAllPaged(): PagingSource<Int, Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(vararg reminders: Reminder): Int

    @Delete
    suspend fun delete(vararg reminders: Reminder): Int
}