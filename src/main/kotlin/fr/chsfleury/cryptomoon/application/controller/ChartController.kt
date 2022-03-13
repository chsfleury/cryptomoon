package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.application.io.formatter.standard.StandardChartDataFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType.ATH
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType.CURRENT
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import io.javalin.http.Context

class ChartController(
    private val portfolioService: PortfolioService,
    private val quoteService: QuoteService,
    private val athService: ATHService,
    private val formatters: Collection<ChartDataFormatter>
) {

    fun getAssetDistribution(ctx: Context) {
        val (fiat, portfolioStats) = getFiatAndPortfolioStats(ctx)
        formatAndRespond(ctx) {
            it.assetDistributionData(portfolioStats, fiat)
        }
    }

    fun getAccountValueDistribution(ctx: Context) {
        val (fiat, portfolioStats) = getFiatAndPortfolioStats(ctx)
        formatAndRespond(ctx) {
            it.accountValueDistribution(portfolioStats, fiat)
        }
    }

    private fun getFiatAndPortfolioStats(ctx: Context): Pair<Fiat, PortfolioStats> {
        val portfolioName = ctx.pathParam("portfolio")
        val fiat = ctx.queryParam("fiat")?.let { Fiat.valueOf(it.uppercase()) } ?: Fiat.EUR
        val portfolio = portfolioService.getPortfolio(portfolioName, false)
        val portfolioStats = portfolio.stats(quoteService, athService, Tickers.COINMARKETCAP)
        return Pair(fiat, portfolioStats)
    }

    fun getPortfolioHistory(ctx: Context) {
        val portfolioValueType = PortfolioValueType.valueOf(ctx.pathParam("valueType").uppercase())
        getPortfolioHistory(portfolioValueType, ctx)
    }

    fun getPortfolioHistory(portfolioValueType: PortfolioValueType, ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val days = ctx.queryParam("days")?.toIntOrNull() ?: 7
        val fiat = ctx.queryParam("fiat")?.let { Fiat.valueOf(it.uppercase()) } ?: Fiat.USD
        val history = portfolioService.getHistory(portfolioName, portfolioValueType, fiat, days)
        formatAndRespond(ctx) { formatter ->
            formatHistoryData(formatter, history)
        }
    }

    private fun formatAndRespond(ctx: Context, responseSupplier: (ChartDataFormatter) -> Any?) {
        chooseFormatter(ctx)
            .let(responseSupplier::invoke)
            ?.run { ctx.json(this) }
            ?: ctx.status(400)
    }

    private fun chooseFormatter(ctx: Context): ChartDataFormatter = ctx.queryParam("format")
        ?.let { format -> formatters.firstOrNull { it.formatName == format }  }
        ?: StandardChartDataFormatter

    private fun formatHistoryData(formatter: ChartDataFormatter, history: PortfolioHistory): Any? = when(history.type) {
        CURRENT -> formatter.valueHistory(history)
        ATH -> formatter.athHistory(history)
    }

}