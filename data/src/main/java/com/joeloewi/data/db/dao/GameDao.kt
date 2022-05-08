package com.joeloewi.data.db.dao

import androidx.room.*
import com.joeloewi.data.entity.GameEntity

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg gameEntity: GameEntity): List<Long>

    @Update
    suspend fun update(vararg gameEntities: GameEntity): Int

    @Delete
    suspend fun delete(vararg gameEntities: GameEntity): Int
}