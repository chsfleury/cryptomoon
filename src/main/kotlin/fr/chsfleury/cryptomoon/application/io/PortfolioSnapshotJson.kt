package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import java.math.BigDecimal

class PortfolioSnapshotJson(
    val value: BigDecimal,
    val at: String
) {
    companion object {
        fun of(portfolioSnapshot: PorfolioValueSnapshot, conversionRate: BigDecimal?) = PortfolioSnapshotJson(
            portfolioSnapshot.valueUSD.applyRate(conversionRate),
            portfolioSnapshot.at.toString()
        )
    }
}