package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.Account
import com.joeloewi.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class AccountUseCase {
    class Insert @Inject constructor(
        private val accountRepository: AccountRepository
    ): AccountUseCase() {
        suspend operator fun invoke(vararg accounts: Account) =
            accountRepository.insert(*accounts)
    }
}
