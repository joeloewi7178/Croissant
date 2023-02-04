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

package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.local.ResinStatusWidgetDataSource
import com.joeloewi.croissant.domain.entity.ResinStatusWidget
import com.joeloewi.croissant.domain.entity.relational.ResinStatusWidgetWithAccounts
import com.joeloewi.croissant.domain.repository.ResinStatusWidgetRepository
import javax.inject.Inject

class ResinStatusWidgetRepositoryImpl @Inject constructor(
    private val resinStatusWidgetDataSource: ResinStatusWidgetDataSource
) : ResinStatusWidgetRepository {
    override suspend fun getAll(): List<ResinStatusWidget> =
        resinStatusWidgetDataSource.getAll()

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