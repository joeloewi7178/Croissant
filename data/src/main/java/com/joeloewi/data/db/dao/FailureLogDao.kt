package com.joeloewi.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.joeloewi.data.entity.local.FailureLogEntity

@Dao
interface FailureLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(failureLogEntity: FailureLogEntity): Long
}