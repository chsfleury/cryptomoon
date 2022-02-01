package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class AccountService(
    private val quoteService: QuoteService,
    private val accountRepositories: Collection<AccountRepository>
) {
    fun allAccounts(): Set<AccountSnapshot> {
        val accountSnapshots = accountRepositories.flatMapTo(mutableListOf(), AccountRepository::allAccounts)
        val collision = accountSnapshots.mapTo(mutableSetOf(), AccountSnapshot::origin).size < accountSnapshots.size
        return if (!collision) {
            accountSnapshots.toSet()
        } else {
            val snapshotMap = mutableMapOf<String, AccountSnapshot>()
            accountSnapshots.forEach { snap ->
                val existingSnap = snapshotMap[snap.origin]
                if (existingSnap == null) {
                    snapshotMap[snap.origin] = snap
                } else {
                    snapshotMap[snap.origin] = AccountSnapshot.merge(listOf(existingSnap, snap), snap.origin) ?: error("cannot happen theorically")
                }
            }
            snapshotMap.values.toSet()
        }
    }

    fun mergedAccounts(origins: Set<String>): AccountSnapshot = accountRepositories.asSequence().flatMap { repo ->
        sequenceOf(repo.mergedAccounts(origins))
    }.toList().let { AccountSnapshot.merge(it, AccountSnapshot.ALL) } ?: error("a merged account cannot be null")


    fun getAccount(origin: String): AccountSnapshot? = accountRepositories.asSequence().flatMap { repo ->
        repo.getAccount(origin)?.let { sequenceOf(it) } ?: emptySequence()
    }.toList().let { AccountSnapshot.merge(it, origin) }

    fun getKnownCurrencies(): Set<Currency> = accountRepositories.flatMapTo(mutableSetOf(), AccountRepository::getKnownCurrencies)

    fun insert(account: AccountSnapshot) = accountRepositories.forEach {
        if (it.supportWrite()) {
            it.insert(account)
        }
    }

    fun computeStats(accountSnapshot: AccountSnapshot, ticker: Tickers): AccountStats {
        val usdToEur: BigDecimal? = quoteService.usdToEur()
        val quotes = quoteService[ticker]
        val total = FiatMap()

        val assetStats = accountSnapshot.balances.asSequence()
            .filterNot(Balance::isZero)
            .map { asset ->
                val balance = asset.amount
                val price = FiatMap()
                val priceUSD = quotes?.get(asset.currency)?.price?.also {
                    price[Fiat.USD] = it
                    usdToEur?.multiply(it)?.also { p -> price[Fiat.EUR] = p }
                }
                val value = FiatMap()
                priceUSD?.multiply(balance)?.also {
                    value[Fiat.USD] = it
                    usdToEur?.multiply(it)?.also { p -> value[Fiat.EUR] = p }
                }
                total += value
                AssetStats(asset.currency, balance, price, value)
            }
            .toSet()

        return AccountStats(accountSnapshot.origin, total, assetStats, accountSnapshot.timestamp)
    }

}