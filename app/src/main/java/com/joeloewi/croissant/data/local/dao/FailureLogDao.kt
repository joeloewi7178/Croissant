package com.joeloewi.croissant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.joeloewi.croissant.data.local.model.FailureLog

@Dao
interface FailureLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(failureLog: FailureLog): Long
}