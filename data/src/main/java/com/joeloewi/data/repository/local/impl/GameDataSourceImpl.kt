package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.GameDao
import com.joeloewi.data.mapper.GameMapper
import com.joeloewi.data.repository.local.GameDataSource
import com.joeloewi.domain.entity.Game
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