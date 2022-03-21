package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalance
import fr.chsfleury.cryptomoon.domain.model.Fiat.EUR
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
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

    fun stats(marketData: MarketData) = accountStats ?: computeStats(marketData)

    private fun computeStats(marketData: MarketData): AccountStats {
        log.debug("compute {} account stats", origin)
        val total = Sum()

        val assetStats = balances.asSequence()
            .filterNot(Balance::isZero)
            .map { asset ->
                computeAssetStats(asset, marketData, total)
            }
            .filterNotNull()
            .toSet()

        return AccountStats(origin, total.value(), assetStats, timestamp)
            .also { accountStats = it }
    }

    private fun computeAssetStats(
        asset: Balance,
        marketData: MarketData,
        total: Sum,
    ): AssetStats? {
        return when (asset.currency) {
            Currencies.EUR.currency -> computeEuroStats(asset.amount, marketData.rates[EUR])
            Currencies.USD.currency -> computeUsdStats(asset.amount)
            else -> computeCryptoStats(marketData, asset, total)
        }
    }

    private fun computeEuroStats(balance: BigDecimal, usdToEur: BigDecimal?): AssetStats? {
        if (usdToEur == null) return null
        val price = BigDecimal.ONE.setScale(4).divide(usdToEur, RoundingMode.HALF_EVEN)
        val value = usdToEur.multiply(balance)
        return AssetStats(Currencies.EUR.currency, null, balance, price, value, price, 100.0)
    }

    private fun computeUsdStats(balance: BigDecimal): AssetStats {
        return AssetStats(Currencies.USD.currency, null, balance, BigDecimal.ONE, balance, BigDecimal.ONE, 100.0)
    }

    private fun computeCryptoStats(
        marketData: MarketData,
        asset: Balance,
        total: Sum,
    ): AssetStats? {
        val currencyData = marketData[asset.currency] ?: return null
        val value = asset.amount.multiply(currencyData.priceUSD)
        val athRatio = 100.0 * currencyData.priceUSD.toDouble() / currencyData.athUSD.toDouble()

        total += value

        return AssetStats(asset.currency, currencyData.rank, asset.amount, currencyData.priceUSD, value, currencyData.athUSD, athRatio)
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