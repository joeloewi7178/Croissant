package com.joeloewi.data.db.dao

import androidx.room.*
import com.joeloewi.data.entity.ResinStatusWidgetEntity
import com.joeloewi.data.entity.relational.ResinStatusWidgetWithAccountsEntity

@Dao
interface ResinStatusWidgetDao {

    @Query("SELECT * FROM ResinStatusWidgetEntity")
    suspend fun getAll(): List<ResinStatusWidgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resinStatusWidgetEntity: ResinStatusWidgetEntity): Long

    @Delete
    suspend fun delete(vararg resinStatusWidgetEntities: ResinStatusWidgetEntity): Int

    @Update
    suspend fun update(resinStatusWidgetEntity: ResinStatusWidgetEntity): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidgetEntity WHERE id = :id")
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccountsEntity

    @Transaction
    @Query("DELETE FROM ResinStatusWidgetEntity WHERE appWidgetId IN (:appWidgetIds)")
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidgetEntity WHERE appWidgetId = :appWidgetId")
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccountsEntity
}