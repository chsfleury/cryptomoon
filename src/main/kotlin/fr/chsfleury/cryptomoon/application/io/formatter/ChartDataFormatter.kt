package fr.chsfleury.cryptomoon.application.io.formatter

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats

interface ChartDataFormatter {

    val formatName: String

    fun assetDistributionData(portfolioStats: PortfolioStats): Any?

    fun accountValueDistribution(portfolioStats: PortfolioStats, fiat: Fiat): Any?

    fun valueHistory(portfolioHistory: PortfolioHistory): Any?

    fun athHistory(portfolioHistory: PortfolioHistory): Any?

}