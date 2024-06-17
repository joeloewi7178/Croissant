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

package com.joeloewi.croissant.core.database

import com.joeloewi.croissant.core.database.dao.ResinStatusWidgetDao
import com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity
import com.joeloewi.croissant.core.database.model.relational.ResinStatusWidgetWithAccountsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResinStatusWidgetDataSourceImpl @Inject constructor(
    private val resinStatusWidgetDao: ResinStatusWidgetDao,
) : ResinStatusWidgetDataSource {

    override suspend fun getAll(): List<ResinStatusWidgetEntity> =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.getAll()
        }

    override suspend fun insert(resinStatusWidget: ResinStatusWidgetEntity): Long =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.insert(resinStatusWidget)
        }

    override suspend fun delete(vararg resinStatusWidgets: ResinStatusWidgetEntity): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.delete(*resinStatusWidgets)
        }

    override suspend fun update(resinStatusWidget: ResinStatusWidgetEntity): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.update(resinStatusWidget)
        }

    override suspend fun getOne(id: Long): ResinStatusWidgetWithAccountsEntity =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.getOne(id)
        }

    override suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.deleteByAppWidgetId(*appWidgetIds)
        }

    override suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccountsEntity =
        withContext(Dispatchers.IO) {
            resinStatusWidgetDao.getOneByAppWidgetId(
                appWidgetId
            )
        }
}