package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.math.BigDecimal

class Portfolio(
    val name: String,
    val accounts: Set<AccountSnapshot>
) {
    val mergedAccount: AccountSnapshot by lazy {
        AccountSnapshot.merge(accounts, AccountSnapshot.ALL) ?: AccountSnapshot.empty()
    }

    private var portfolioStats: PortfolioStats? = null

    fun stats(quoteService: QuoteService, athService: ATHService, ticker: Tickers): PortfolioStats = portfolioStats ?: computeStats(quoteService, athService, ticker)

    private fun computeStats(quoteService: QuoteService, athService: ATHService, ticker: Tickers): PortfolioStats {
        log.debug("computing {} portfolio stats", name)
        val total = FiatMap()
        val accountStatsSet = accounts.mapTo(mutableSetOf()) { it.stats(quoteService, ticker) }
        accountStatsSet.forEach { total += it.total }
        val mergedAccountStats = mergedAccount.stats(quoteService, ticker)
        val athTotalInUSD = accountStatsSet.asSequence()
            .flatMap(AccountStats::assetStats)
            .map { assetStats -> assetStats.balance * (athService[assetStats.currency] ?: BigDecimal.ZERO) }
            .sumOf { it }
        val athFiatMap = FiatMap()
        athFiatMap[Fiat.USD] = athTotalInUSD
        quoteService.usdToEur()?.also { usdToEur ->
            athFiatMap[Fiat.EUR] = athTotalInUSD * usdToEur
        }
        return PortfolioStats(name, total, athFiatMap, accountStatsSet, mergedAccountStats)
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