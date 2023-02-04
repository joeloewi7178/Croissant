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

import com.joeloewi.croissant.data.repository.local.GameDataSource
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDataSource: GameDataSource
) : GameRepository {
    override suspend fun insert(vararg games: Game): List<Long> =
        gameDataSource.insert(*games)

    override suspend fun update(vararg games: Game): Int =
        gameDataSource.update(*games)

    override suspend fun delete(vararg games: Game): Int =
        gameDataSource.delete(*games)
}