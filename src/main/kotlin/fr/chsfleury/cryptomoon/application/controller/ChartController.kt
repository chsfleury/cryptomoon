package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.controller.utils.QueryParams.fiatQueryParam
import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.application.io.formatter.standard.StandardChartDataFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType.ATH
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType.CURRENT
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import io.javalin.http.Context
import java.math.BigDecimal

class ChartController(
    private val portfolioService: PortfolioService,
    private val currencyService: CurrencyService,
    private val formatters: Collection<ChartDataFormatter>
) {

    fun getAssetDistribution(ctx: Context) {
        formatAndRespond(ctx) {
            it.assetDistributionData(getFiatAndPortfolioStats(ctx), getConversionRate(ctx))
        }
    }

    fun getAccountValueDistribution(ctx: Context) {
        formatAndRespond(ctx) {
            it.accountValueDistribution(getFiatAndPortfolioStats(ctx), getConversionRate(ctx))
        }
    }

    private fun getFiatAndPortfolioStats(ctx: Context): PortfolioStats {
        val portfolioName = ctx.pathParam("portfolio")
        val portfolio = portfolioService.getPortfolio(portfolioName, false)
        val marketData = currencyService.marketData()
        return portfolio.stats(marketData)
    }

    fun getPortfolioHistory(ctx: Context) {
        val portfolioValueType = PortfolioValueType.valueOf(ctx.pathParam("valueType").uppercase())
        getPortfolioHistory(portfolioValueType, ctx)
    }

    fun getPortfolioHistory(portfolioValueType: PortfolioValueType, ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val days = ctx.queryParam("days")?.toIntOrNull() ?: 7
        val history = portfolioService.getHistory(portfolioName, portfolioValueType, days)
        formatAndRespond(ctx) { formatter ->
            formatHistoryData(formatter, history, getConversionRate(ctx))
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

    private fun formatHistoryData(formatter: ChartDataFormatter, history: PortfolioHistory, conversionRate: BigDecimal?): Any? = when(history.type) {
        CURRENT -> formatter.valueHistory(history, conversionRate)
        ATH -> formatter.athHistory(history, conversionRate)
    }

    private fun getConversionRate(ctx: Context): BigDecimal? = ctx.fiatQueryParam().takeIf { it != Fiat.USD }?.let { currencyService.usdToFiat(it) }

}