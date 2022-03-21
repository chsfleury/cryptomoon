package fr.chsfleury.cryptomoon.application.page

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.BalanceService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import io.javalin.http.Context

class DashboardPage(
    private val portfolioService: PortfolioService,
    private val currencyService: CurrencyService,
    private val balanceService: BalanceService
) {

    fun getDashboard(ctx: Context) {
        val portfolios = portfolioService.getPortfolios(false)
        val portfolioStats = portfolios.asSequence()
            .map { it.stats(currencyService.marketData()) }
            .sortedBy(PortfolioStats::name)
            .toList()

        val mergedStats = portfolios.associate { portfolio ->
            val allAccountSnapshot = AccountSnapshot.merge(portfolio.accounts, AccountSnapshot.ALL) ?: AccountSnapshot.empty()
            val quotes = currencyService.marketData()
            portfolio.name to allAccountSnapshot.stats(quotes)
        }
        val deltaMap = portfolios.associate { portfolio ->
            val accountNames = portfolioService.getPorfolioAccountNames(portfolio.name)
            portfolio.name to Deltas(balanceService.getDelta(accountNames, 7).sortedByDescending { it.valueUSD })
        }
        ctx.render("dashboard.html", mapOf(
            "portfolios" to portfolioStats,
            "mergedMap" to mergedStats,
            "deltaMap" to deltaMap,
            "bitcoinPriceUSD" to currencyService.marketData()[Currencies["BTC"]]?.priceUSD
        ))
    }

}