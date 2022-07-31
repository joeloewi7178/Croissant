package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.ResinStatusWidgetDao
import com.joeloewi.data.mapper.ResinStatusWidgetMapper
import com.joeloewi.data.mapper.ResinStatusWithAccountsMapper
import com.joeloewi.data.repository.local.ResinStatusWidgetDataSource
import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.entity.relational.ResinStatusWidgetWithAccounts
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResinStatusWidgetDataSourceImpl @Inject constructor(
    private val resinStatusWidgetDao: ResinStatusWidgetDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val resinStatusWidgetMapper: ResinStatusWidgetMapper,
    private val resinStatusWidgetWithAccountsMapper: ResinStatusWithAccountsMapper
) : ResinStatusWidgetDataSource {

    override suspend fun getAll(): List<ResinStatusWidget> = withContext(coroutineDispatcher) {
        resinStatusWidgetDao.getAll().map { resinStatusWidgetMapper.toDomain(it) }
    }

    override suspend fun insert(resinStatusWidget: ResinStatusWidget): Long =
        withContext(coroutineDispatcher) {
            resinStatusWidgetDao.insert(resinStatusWidgetMapper.toData(resinStatusWidget))
        }

    override suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int =
        withContext(coroutineDispatcher) {
            resinStatusWidgetDao.delete(*resinStatusWidgets.map { resinStatusWidgetMapper.toData(it) }
                .toTypedArray())
        }

    override suspend fun update(resinStatusWidget: ResinStatusWidget): Int =
        withContext(coroutineDispatcher) {
            resinStatusWidgetDao.update(resinStatusWidgetMapper.toData(resinStatusWidget))
        }

    override suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts =
        withContext(coroutineDispatcher) {
            resinStatusWidgetWithAccountsMapper.toDomain(resinStatusWidgetDao.getOne(id))
        }

    override suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int =
        withContext(coroutineDispatcher) {
            resinStatusWidgetDao.deleteByAppWidgetId(*appWidgetIds)
        }

    override suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts =
        withContext(coroutineDispatcher) {
            resinStatusWidgetWithAccountsMapper.toDomain(
                resinStatusWidgetDao.getOneByAppWidgetId(
                    appWidgetId
                )
            )
        }
}