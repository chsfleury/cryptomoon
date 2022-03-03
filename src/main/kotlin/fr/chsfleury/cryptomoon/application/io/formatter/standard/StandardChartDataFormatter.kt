package fr.chsfleury.cryptomoon.application.io.formatter.standard

import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats

object StandardChartDataFormatter: ChartDataFormatter {
    override val formatName = "standard"

    override fun assetDistributionData(portfolioStats: PortfolioStats): List<KeyedValue<String>> = portfolioStats.mergedAccountStats.assetsByValueDesc
        .map { KeyedValue(it.currency.symbol, it.value[Fiat.USD]?.toDouble() ?: 0.0) }

    override fun accountValueDistribution(portfolioStats: PortfolioStats, fiat: Fiat): List<KeyedValue<String>> = portfolioStats.accountStats.asSequence()
        .filter { it.total[fiat] != null }
        .map { KeyedValue(it.origin, it.total.clean()[fiat]!!.toDouble()) }
        .toList()

    override fun valueHistory(portfolioHistory: PortfolioHistory): PortfolioHistoryJson = PortfolioHistoryJson.of(portfolioHistory)

    override fun athHistory(portfolioHistory: PortfolioHistory): PortfolioHistoryJson = PortfolioHistoryJson.of(portfolioHistory)
}