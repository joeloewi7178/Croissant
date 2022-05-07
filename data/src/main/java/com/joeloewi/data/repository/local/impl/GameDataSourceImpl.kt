package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.GameDao
import com.joeloewi.data.entity.GameEntity
import com.joeloewi.data.mapper.GameMapper
import com.joeloewi.data.repository.local.GameDataSource
import com.joeloewi.domain.entity.Game
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameDataSourceImpl @Inject constructor(
    private val gameDao: GameDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val gameMapper: GameMapper,
) : GameDataSource {

    override suspend fun insert(vararg games: Game): List<Long> =
        withContext(coroutineDispatcher) {
            gameDao.insert(*games.map { gameMapper.toData(it) }.toTypedArray())
        }

    override suspend fun update(vararg games: Game): Int =
        withContext(coroutineDispatcher) {
            gameDao.update(*games.map { gameMapper.toData(it) }.toTypedArray())
        }

    override suspend fun delete(vararg games: Game): Int =
        withContext(coroutineDispatcher) {
            gameDao.delete(*games.map { gameMapper.toData(it) }.toTypedArray())
        }
}