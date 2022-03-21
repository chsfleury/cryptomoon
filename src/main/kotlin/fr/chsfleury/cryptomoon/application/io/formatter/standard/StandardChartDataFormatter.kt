package fr.chsfleury.cryptomoon.application.io.formatter.standard

import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import java.math.BigDecimal

object StandardChartDataFormatter: ChartDataFormatter {
    override val formatName = "standard"

    override fun assetDistributionData(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): List<KeyedValue<String>> = portfolioStats.mergedAccountStats.assetsByValueDesc
        .map { KeyedValue(it.currency.symbol, it.valueUSD.applyRate(conversionRate).toDouble()) }

    override fun accountValueDistribution(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): List<KeyedValue<String>> = portfolioStats.accountStats
        .map { KeyedValue(it.origin, it.total.applyRate(conversionRate).clean().toDouble()) }

    override fun valueHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): PortfolioHistoryJson = PortfolioHistoryJson.of(portfolioHistory, conversionRate)

    override fun athHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): PortfolioHistoryJson = PortfolioHistoryJson.of(portfolioHistory, conversionRate)
}