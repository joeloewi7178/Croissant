package com.joeloewi.croissant.data.local.dao

import androidx.room.*
import com.joeloewi.croissant.data.local.model.ResinStatusWidget
import com.joeloewi.croissant.data.local.model.relational.ResinStatusWidgetWithAccounts

@Dao
interface ResinStatusWidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resinStatusWidget: ResinStatusWidget): Long

    @Delete
    suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int

    @Update
    suspend fun update(resinStatusWidget: ResinStatusWidget): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidget WHERE id = :id")
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts

    @Transaction
    @Query("DELETE FROM ResinStatusWidget WHERE appWidgetId IN (:appWidgetIds)")
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int

    @Transaction
    @Query("SELECT * FROM ResinStatusWidget WHERE appWidgetId = :appWidgetId")
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts
}