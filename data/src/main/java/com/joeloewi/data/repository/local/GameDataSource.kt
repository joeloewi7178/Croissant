package com.joeloewi.data.repository.local

import com.joeloewi.domain.entity.Game

interface GameDataSource {
    suspend fun insert(vararg games: Game): List<Long>
    suspend fun update(vararg games: Game): Int
    suspend fun delete(vararg games: Game): Int
}