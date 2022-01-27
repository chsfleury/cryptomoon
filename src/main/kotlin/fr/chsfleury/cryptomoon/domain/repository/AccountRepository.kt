package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currency

interface AccountRepository {

    fun mergedAccounts(origins: Set<String>): AccountSnapshot
    fun allAccounts(): Set<AccountSnapshot>
    fun getAccount(origin: String): AccountSnapshot?
    fun getKnownCurrencies(): Set<Currency>

    fun insert(account: AccountSnapshot)

}