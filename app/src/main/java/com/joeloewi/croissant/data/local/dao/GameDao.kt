package com.joeloewi.croissant.data.local.dao

import androidx.room.*
import com.joeloewi.croissant.data.local.model.Game

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg game: Game): List<Long>

    @Update
    suspend fun update(vararg games: Game): Int

    @Delete
    suspend fun delete(vararg games: Game): Int
}