package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.entity.relational.ResinStatusWidgetWithAccounts

interface ResinStatusWidgetRepository {
    suspend fun getAll(): List<ResinStatusWidget>
    suspend fun insert(resinStatusWidget: ResinStatusWidget): Long
    suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int
    suspend fun update(resinStatusWidget: ResinStatusWidget): Int
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts
}