package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.repository.GameRepository
import javax.inject.Inject

sealed class GameUseCase {
    class Insert @Inject constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.insert(*game)
    }

    class Update @Inject constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.insert(*game)
    }

    class Delete @Inject constructor(
        private val gameRepository: GameRepository
    ) : GameUseCase() {
        suspend operator fun invoke(vararg game: Game) = gameRepository.delete(*game)
    }
}
