package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository

class AccountService(
    private val accountRepository: AccountRepository
) {
    fun allAccounts(): Set<AccountSnapshot> = accountRepository.allAccounts()
    fun mergedAccounts(origins: Set<String>): AccountSnapshot = accountRepository.mergedAccounts(origins)
    fun getAccount(origin: String): AccountSnapshot? = accountRepository.getAccount(origin)
    fun getKnownCurrencies(): Set<Currency> = accountRepository.getKnownCurrencies()

    fun insert(account: AccountSnapshot) = accountRepository.insert(account)

}