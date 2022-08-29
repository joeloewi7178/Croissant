package com.joeloewi.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.joeloewi.data.entity.local.SuccessLogEntity

@Dao
interface SuccessLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(successLogEntity: SuccessLogEntity): Long
}