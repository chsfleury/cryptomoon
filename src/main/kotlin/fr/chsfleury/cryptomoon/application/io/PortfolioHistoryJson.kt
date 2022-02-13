package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType

class PortfolioHistoryJson(
    val type: PortfolioValueType,
    val fiat: Fiat,
    val data: List<PortfolioSnapshotJson>
) {
    companion object {
        fun of(portfolioHistory: PortfolioHistory) = PortfolioHistoryJson(
            portfolioHistory.type,
            portfolioHistory.fiat,
            portfolioHistory.snapshots.map { PortfolioSnapshotJson.of(it) }
        )
    }
}