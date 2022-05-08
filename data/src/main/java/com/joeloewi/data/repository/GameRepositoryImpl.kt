package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.GameDataSource
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.repository.GameRepository
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