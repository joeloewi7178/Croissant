package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.repository.GameRepository

sealed class GameUseCase {
    class Insert constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.insert(*game)
    }

    class Update constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.insert(*game)
    }

    class Delete constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.delete(*game)
    }
}
