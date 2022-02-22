package fr.chsfleury.cryptomoon.application.page

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers.COINMARKETCAP
import io.javalin.http.Context

class DashboardPage(
    private val portfolioService: PortfolioService,
    private val quoteService: QuoteService,
    private val athService: ATHService
) {

    fun getDashboard(ctx: Context) {
        val portfolios = portfolioService.getPortfolios(false)
        val portfolioStats = portfolios.asSequence()
            .map { it.stats(quoteService, athService, COINMARKETCAP) }
            .sortedBy(PortfolioStats::name)
            .toList()
        val mergedStats = portfolios.associate { portfolio ->
            val allAccountSnapshot = AccountSnapshot.merge(portfolio.accounts, AccountSnapshot.ALL) ?: AccountSnapshot.empty()
            portfolio.name to allAccountSnapshot.stats(quoteService, athService, COINMARKETCAP)
        }
        ctx.render("dashboard.html", mapOf(
            "portfolios" to portfolioStats,
            "mergedMap" to mergedStats,
            "bitcoinPriceUSD" to quoteService[COINMARKETCAP]?.get(Currencies["BTC"])?.price
        ))
    }

}