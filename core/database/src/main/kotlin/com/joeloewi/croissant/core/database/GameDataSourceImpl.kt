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

import com.joeloewi.croissant.core.database.dao.GameDao
import com.joeloewi.croissant.core.database.model.GameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameDataSourceImpl @Inject constructor(
    private val gameDao: GameDao
) : GameDataSource {

    override suspend fun insert(vararg games: GameEntity): List<Long> =
        withContext(Dispatchers.IO) {
            gameDao.insert(*games)
        }

    override suspend fun update(vararg games: GameEntity): Int = withContext(Dispatchers.IO) {
        gameDao.update(*games)
    }

    override suspend fun delete(vararg games: GameEntity): Int = withContext(Dispatchers.IO) {
        gameDao.delete(*games)
    }
}