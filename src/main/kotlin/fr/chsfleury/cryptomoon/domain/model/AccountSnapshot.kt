package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalance
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.math.BigDecimal
import java.time.Instant

class AccountSnapshot (
    val origin: String,
    val balances: Set<Balance>,
    val timestamp: Instant
) {
    private var accountStats: AccountStats? = null

    fun stats(quoteService: QuoteService, ticker: Tickers) = accountStats ?: computeStats(quoteService, ticker)

    private fun computeStats(quoteService: QuoteService, ticker: Tickers): AccountStats {
        log.debug("compute {} account stats", origin)
        val usdToEur: BigDecimal? = quoteService.usdToEur()
        val quotes = quoteService[ticker]
        val total = FiatMap()

        val assetStats = balances.asSequence()
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

        return AccountStats(origin, total, assetStats, timestamp)
            .also { accountStats = it }
    }

    companion object: Logging {
        val log = logger()
        const val ALL = "ALL"

        fun empty() = AccountSnapshot("", emptySet(), Instant.now())

        fun of(origin: String, timestamp: Instant, vararg balances: Collection<Balance>): AccountSnapshot = ofSeq(origin, timestamp, balances.asSequence())

        private fun ofSeq(origin: String, timestamp: Instant, sequence: Sequence<Collection<Balance>>): AccountSnapshot {
            val balanceMap = mutableMapOf<Currency, BigDecimal>()
            sequence
                .flatMap { it }
                .forEach {
                    balanceMap.compute(it.currency) { _, old ->
                        old?.plus(it.amount) ?: it.amount
                    }
                }
            val balanceSet = balanceMap.asSequence().sortedBy { it.key.symbol }.mapTo(LinkedHashSet()) { it.toBalance() }
            return AccountSnapshot(origin, balanceSet, timestamp)
        }

        fun merge(accountSnapshots: Collection<AccountSnapshot>, origin: String? = null): AccountSnapshot? {
            if (accountSnapshots.isEmpty()) {
                return null
            }
            val finalOrigin = origin ?: accountSnapshots.asSequence()
                .map(AccountSnapshot::origin)
                .sorted()
                .joinToString("~")

            val timestamp = accountSnapshots.minOf(AccountSnapshot::timestamp)
            return of(finalOrigin, timestamp, accountSnapshots.flatMap(AccountSnapshot::balances))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountSnapshot

        if (origin != other.origin) return false

        return true
    }

    override fun hashCode(): Int {
        return origin.hashCode()
    }
}