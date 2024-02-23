/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.data.repository.local.impl

import com.joeloewi.croissant.data.database.dao.ResinStatusWidgetDao
import com.joeloewi.croissant.data.mapper.ResinStatusWidgetMapper
import com.joeloewi.croissant.data.mapper.ResinStatusWithAccountsMapper
import com.joeloewi.croissant.data.repository.local.ResinStatusWidgetDataSource
import com.joeloewi.croissant.domain.entity.ResinStatusWidget
import com.joeloewi.croissant.domain.entity.relational.ResinStatusWidgetWithAccounts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResinStatusWidgetDataSourceImpl @Inject constructor(
    private val resinStatusWidgetDao: ResinStatusWidgetDao,
    private val resinStatusWidgetMapper: ResinStatusWidgetMapper,
    private val resinStatusWidgetWithAccountsMapper: ResinStatusWithAccountsMapper
) : ResinStatusWidgetDataSource {

    override suspend fun getAll(): List<ResinStatusWidget> =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.getAll().map { resinStatusWidgetMapper.toDomain(it) }
        }

    override suspend fun insert(resinStatusWidget: ResinStatusWidget): Long =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.insert(resinStatusWidgetMapper.toData(resinStatusWidget))
        }

    override suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.delete(*resinStatusWidgets.map { resinStatusWidgetMapper.toData(it) }
                .toTypedArray())
        }

    override suspend fun update(resinStatusWidget: ResinStatusWidget): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.update(resinStatusWidgetMapper.toData(resinStatusWidget))
        }

    override suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts =
        withContext(Dispatchers.IO) {
            resinStatusWidgetWithAccountsMapper.toDomain(resinStatusWidgetDao.getOne(id))
        }

    override suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.deleteByAppWidgetId(*appWidgetIds)
        }

    override suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts =
        withContext(Dispatchers.IO) {
            resinStatusWidgetWithAccountsMapper.toDomain(
                resinStatusWidgetDao.getOneByAppWidgetId(
                    appWidgetId
                )
            )
        }
}