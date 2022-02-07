package fr.chsfleury.cryptomoon.application.page

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import io.javalin.http.Context

class DashboardPage(
    private val portfolioService: PortfolioService,
    private val accountService: AccountService
) {

    fun getDashboard(ctx: Context) {
        val portfolios = portfolioService.getPortfolios(false)
        val portfolioStats = portfolios.asSequence()
            .map { portfolioService.computeStats(it, Tickers.COINMARKETCAP) }
            .sortedBy(PortfolioStats::name)
            .toList()
        val mergedStats = portfolios.associate { portfolio ->
            val allAccountSnapshot = AccountSnapshot.merge(portfolio.accounts, AccountSnapshot.ALL) ?: AccountSnapshot.empty()
            portfolio.name to accountService.computeStats(allAccountSnapshot, Tickers.COINMARKETCAP)
        }
        ctx.render("dashboard.html", mapOf(
            "portfolios" to portfolioStats,
            "mergedMap" to mergedStats
        ))
    }

}