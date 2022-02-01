package fr.chsfleury.cryptomoon.infrastructure.repository.file

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalance
import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalances
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository
import fr.chsfleury.cryptomoon.infrastructure.wallet.LocalWalletsFile
import java.time.Instant

object LocalFileAccountRepository: AccountRepository {

    override fun mergedAccounts(origins: Set<String>): AccountSnapshot {
        val balanceList = LocalWalletsFile.getWallets().values
            .asSequence()
            .flatMap { wallet -> wallet.balances.toBalances() }
            .toList()

        return AccountSnapshot.of(AccountSnapshot.ALL, Instant.now(), balanceList)
    }

    override fun allAccounts(): Set<AccountSnapshot> {
        val now = Instant.now()
        return LocalWalletsFile.getWallets().mapTo(mutableSetOf()) {
            val balances = it.value.balances.mapTo(mutableSetOf()) { b -> b.toBalance() }
            AccountSnapshot(it.key, balances, now)
        }
    }

    override fun getAccount(origin: String): AccountSnapshot? {
        val now = Instant.now()
        return LocalWalletsFile.getWallets()[origin]?.let { wallet ->
            val balances = wallet.balances.mapTo(mutableSetOf()) { b -> b.toBalance() }
            AccountSnapshot(wallet.name, balances, now)
        }
    }

    override fun getKnownCurrencies(): Set<Currency> {
        return LocalWalletsFile.getWallets().values
            .asSequence()
            .flatMap { it.balances.keys }
            .toSet()
    }

    override fun supportWrite(): Boolean = false

    override fun insert(account: AccountSnapshot) {
        error("not supported")
    }
}