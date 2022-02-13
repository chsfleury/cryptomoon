package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import java.math.BigDecimal

class PortfolioSnapshotJson(
    val value: BigDecimal,
    val at: String
) {
    companion object {
        fun of(portfolioSnapshot: PorfolioValueSnapshot) = PortfolioSnapshotJson(
            portfolioSnapshot.amount,
            portfolioSnapshot.at.toString()
        )
    }
}