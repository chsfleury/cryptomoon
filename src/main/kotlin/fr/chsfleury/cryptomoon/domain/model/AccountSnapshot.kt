package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalance
import fr.chsfleury.cryptomoon.domain.model.Fiat.EUR
import fr.chsfleury.cryptomoon.domain.model.Fiat.USD
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

class AccountSnapshot(
    val origin: String,
    val balances: Set<Balance>,
    val timestamp: Instant
) {
    private var accountStats: AccountStats? = null

    fun stats(quoteService: QuoteService, athService: ATHService, ticker: Tickers) = accountStats ?: computeStats(quoteService, athService, ticker)

    private fun computeStats(quoteService: QuoteService, athService: ATHService, ticker: Tickers): AccountStats {
        log.debug("compute {} account stats", origin)
        val usdToEur: BigDecimal? = quoteService.usdToEur()
        val quotes = quoteService[ticker]
        val total = FiatMap()

        val assetStats = balances.asSequence()
            .filterNot(Balance::isZero)
            .map { asset ->
                computeAssetStats(asset, quotes, usdToEur, total, athService)
            }
            .toSet()

        return AccountStats(origin, total, assetStats, timestamp)
            .also { accountStats = it }
    }

    private fun computeAssetStats(
        asset: Balance,
        quotes: Quotes?,
        usdToEur: BigDecimal?,
        total: FiatMap,
        athService: ATHService
    ): AssetStats {
        val balance = asset.amount
        val price = FiatMap()

        return when (asset.currency) {
            Currencies.EUR.currency -> computeEuroStats(balance, usdToEur)
            Currencies.USD.currency -> computeUsdStats(balance, usdToEur)
            else -> computeCryptoStats(quotes, asset, price, usdToEur, balance, total, athService)
        }
    }

    private fun computeEuroStats(balance: BigDecimal, usdToEur: BigDecimal?): AssetStats {
        val price = usdToEur?.let { usd2eur ->
            FiatMap.of(
                EUR to BigDecimal.ONE,
                USD to BigDecimal.ONE.setScale(4).divide(usd2eur, RoundingMode.HALF_EVEN)
            )
        } ?: FiatMap.of(EUR to BigDecimal.ONE)

        val value = usdToEur?.let { usd2eur ->
            FiatMap.of(
                EUR to balance,
                USD to balance.divide(usd2eur, RoundingMode.HALF_EVEN)
            )
        } ?: FiatMap.of(EUR to balance)

        return AssetStats(Currencies.EUR.currency, null, balance, price, value, price, 100.0)
    }

    private fun computeUsdStats(balance: BigDecimal, usdToEur: BigDecimal?): AssetStats {
        val price = usdToEur?.let { usd2eur ->
            FiatMap.of(
                EUR to BigDecimal.ONE.multiply(usd2eur),
                USD to BigDecimal.ONE
            )
        } ?: FiatMap.of(USD to BigDecimal.ONE)

        val value = usdToEur?.let { usd2eur ->
            FiatMap.of(
                EUR to balance.multiply(usd2eur),
                USD to balance
            )
        } ?: FiatMap.of(USD to balance)

        return AssetStats(Currencies.USD.currency, null, balance, price, value, price, 100.0)
    }

    private fun computeCryptoStats(
        quotes: Quotes?,
        asset: Balance,
        price: FiatMap,
        usdToEur: BigDecimal?,
        balance: BigDecimal,
        total: FiatMap,
        athService: ATHService
    ): AssetStats {
        val quote = quotes?.get(asset.currency)
        val priceUSD = quote?.price?.also {
            price[USD] = it
            usdToEur?.multiply(it)?.also { p -> price[EUR] = p }
        }
        val value = FiatMap()
        priceUSD?.multiply(balance)?.also {
            value[USD] = it
            usdToEur?.multiply(it)?.also { p -> value[EUR] = p }
        }
        total += value

        val athMap = athService[asset.currency]?.let { ath ->
            val map = FiatMap.of(USD to ath)
            usdToEur?.run { map[EUR] = this }
            map
        } ?: FiatMap()
        val athRatio = if (athMap[USD] != null && priceUSD != null) {
            100.0 * priceUSD.toDouble() / athMap[USD]!!.toDouble()
        } else null

        return AssetStats(asset.currency, quote?.rank, balance, price, value, athMap, athRatio)
    }

    companion object : Logging {
        val log = logger()
        const val ALL = "ALL"

        fun empty() = AccountSnapshot("", emptySet(), Instant.now())

        fun of(origin: String, timestamp: Instant, vararg balances: Collection<Balance>): AccountSnapshot =
            ofSeq(origin, timestamp, balances.asSequence())

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