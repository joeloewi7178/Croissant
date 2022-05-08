package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.ResinStatusWidgetDataSource
import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.entity.relational.ResinStatusWidgetWithAccounts
import com.joeloewi.domain.repository.ResinStatusWidgetRepository
import javax.inject.Inject

class ResinStatusWidgetRepositoryImpl @Inject constructor(
    private val resinStatusWidgetDataSource: ResinStatusWidgetDataSource
) : ResinStatusWidgetRepository {

    override suspend fun insert(resinStatusWidget: ResinStatusWidget): Long =
        resinStatusWidgetDataSource.insert(resinStatusWidget)

    override suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int =
        resinStatusWidgetDataSource.delete(*resinStatusWidgets)

    override suspend fun update(resinStatusWidget: ResinStatusWidget): Int =
        resinStatusWidgetDataSource.update(resinStatusWidget)

    override suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts =
        resinStatusWidgetDataSource.getOne(id)

    override suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int =
        resinStatusWidgetDataSource.deleteByAppWidgetId(*appWidgetIds)

    override suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts =
        resinStatusWidgetDataSource.getOneByAppWidgetId(appWidgetId)
}