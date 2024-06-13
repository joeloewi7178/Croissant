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

package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.ResinStatusWidget
import com.joeloewi.croissant.core.data.model.relational.ResinStatusWidgetWithAccounts

interface ResinStatusWidgetRepository {
    suspend fun getAll(): List<ResinStatusWidget>
    suspend fun insert(resinStatusWidget: ResinStatusWidget): Long
    suspend fun delete(vararg resinStatusWidgets: ResinStatusWidget): Int
    suspend fun update(resinStatusWidget: ResinStatusWidget): Int
    suspend fun getOne(id: Long): ResinStatusWidgetWithAccounts
    suspend fun deleteByAppWidgetId(vararg appWidgetIds: Int): Int
    suspend fun getOneByAppWidgetId(appWidgetId: Int): ResinStatusWidgetWithAccounts
}