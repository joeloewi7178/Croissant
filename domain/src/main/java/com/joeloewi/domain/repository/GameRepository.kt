package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.Game

interface GameRepository {
    suspend fun insert(vararg games: Game): List<Long>
    suspend fun update(vararg games: Game): Int
    suspend fun delete(vararg games: Game): Int
}