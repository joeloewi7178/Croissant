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

import com.joeloewi.croissant.core.database.model.ResinStatusWidgetEntity
import com.joeloewi.croissant.core.database.model.relational.ResinStatusWidgetWithAccountsEntity

interface ResinStatusWidgetDataSource {
    suspend fun getAll(): List<ResinStatusWidgetEntity>
    suspend fun insert(resinStatusWidget: ResinStatusWidgetEntity): Long
    suspend fun delete(vararg resinStatusWidgets: ResinStatusWidgetEntity): Int
    suspend fun update(resinStatusWidget: ResinStatusWidgetEntity): Int
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccountsEntity
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccountsEntity
}