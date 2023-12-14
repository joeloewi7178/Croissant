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

import com.joeloewi.croissant.data.database.dao.GameDao
import com.joeloewi.croissant.data.mapper.GameMapper
import com.joeloewi.croissant.data.repository.local.GameDataSource
import com.joeloewi.croissant.domain.entity.Game
import javax.inject.Inject

class GameDataSourceImpl @Inject constructor(
    private val gameDao: GameDao,
    private val gameMapper: GameMapper,
) : GameDataSource {

    override suspend fun insert(vararg games: Game): List<Long> =
        gameDao.insert(*games.map { gameMapper.toData(it) }.toTypedArray())

    override suspend fun update(vararg games: Game): Int =
        gameDao.update(*games.map { gameMapper.toData(it) }.toTypedArray())

    override suspend fun delete(vararg games: Game): Int =
        gameDao.delete(*games.map { gameMapper.toData(it) }.toTypedArray())
}