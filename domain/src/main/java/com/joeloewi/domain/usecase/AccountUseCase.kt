package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Account
import com.joeloewi.domain.repository.AccountRepository

sealed class AccountUseCase {
    class Insert constructor(
        private val accountRepository: AccountRepository
    ): AccountUseCase() {
        suspend operator fun invoke(vararg accounts: Account) =
            accountRepository.insert(*accounts)
    }
}
