package fr.chsfleury.cryptomoon.application.io.formatter

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import java.math.BigDecimal

interface ChartDataFormatter {

    val formatName: String

    fun assetDistributionData(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): Any?

    fun accountValueDistribution(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): Any?

    fun valueHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): Any?

    fun athHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): Any?

}