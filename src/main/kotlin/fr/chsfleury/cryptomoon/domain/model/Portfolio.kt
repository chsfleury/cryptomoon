package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

class Portfolio(
    val name: String,
    val accounts: Set<AccountSnapshot>
) {
    private val mergedAccount: AccountSnapshot by lazy {
        AccountSnapshot.merge(accounts, AccountSnapshot.ALL) ?: AccountSnapshot.empty()
    }

    private var portfolioStats: PortfolioStats? = null

    fun stats(marketData: MarketData): PortfolioStats = portfolioStats ?: computeStats(marketData)

    private fun computeStats(marketData: MarketData): PortfolioStats {
        log.debug("computing {} portfolio stats", name)
        val total = Sum()
        val accountStatsSet = accounts.mapTo(mutableSetOf()) { it.stats(marketData) }
        accountStatsSet.forEach { total += it.total }
        val mergedAccountStats = mergedAccount.stats(marketData)
        val athTotalInUSD = accountStatsSet.asSequence()
            .flatMap(AccountStats::assetStats)
            .map { assetStats -> assetStats.balance.multiply(assetStats.athUSD) }
            .sumOf { it }
        return PortfolioStats(name, total.value(), athTotalInUSD, accountStatsSet, mergedAccountStats)
            .also { portfolioStats = it  }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Portfolio

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object: Logging {
        val log = logger()
    }
}