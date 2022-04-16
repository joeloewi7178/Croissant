package com.joeloewi.croissant.data.local.dao

import androidx.room.*
import com.joeloewi.croissant.data.local.model.SuccessLog

@Dao
interface SuccessLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(successLog: SuccessLog): Long
}