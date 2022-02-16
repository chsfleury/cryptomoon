package fr.chsfleury.cryptomoon.application.io.formatter.highcharts

import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats

object HighchartsFormatter: ChartDataFormatter {

    override val formatName = "highcharts"

    override fun assetDistributionData(portfolioStats: PortfolioStats): List<Point> = portfolioStats.mergedAccountStats.assetsByValueDesc
        .map { Point(it.currency.symbol, it.value[Fiat.USD]?.toDouble() ?: 0.0) }

    override fun valueHistory(portfolioHistory: PortfolioHistory): List<List<Any>> = portfolioHistory.snapshots.map {
        listOf(
            it.at.toEpochMilli(),
            it.amount,
            it.amount,
            it.amount,
            it.amount
        )
    }
}