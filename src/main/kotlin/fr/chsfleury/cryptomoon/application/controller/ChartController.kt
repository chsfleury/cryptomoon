package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.PortfolioHistoryJson
import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.HighchartsFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
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
        val portfolioName = ctx.pathParam("portfolio")
        val formatter = ctx.queryParam("format")
            ?.let { format -> formatters.firstOrNull { it.formatName == format } }
            ?: HighchartsFormatter

        val portfolio = portfolioService.getPortfolio(portfolioName, false)
        val portfolioStats = portfolio.stats(quoteService, athService, Tickers.COINMARKETCAP)
        formatter.assetDistributionData(portfolioStats)
            ?.run { ctx.json(this) }
            ?: ctx.status(400)
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
        val responseBody = ctx.queryParam("format")
            ?.let { format ->
                formatters.firstOrNull { it.formatName == format }
            }
            ?.valueHistory(history)
            ?: PortfolioHistoryJson.of(history)

        ctx.json(responseBody)
    }

}