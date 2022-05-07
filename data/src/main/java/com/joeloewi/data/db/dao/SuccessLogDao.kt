package com.joeloewi.data.db.dao

import androidx.room.*
import com.joeloewi.data.entity.SuccessLogEntity

@Dao
interface SuccessLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(successLogEntity: SuccessLogEntity): Long
}