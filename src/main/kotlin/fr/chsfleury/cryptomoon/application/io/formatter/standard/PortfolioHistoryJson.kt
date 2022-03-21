package fr.chsfleury.cryptomoon.application.io.formatter.standard

import fr.chsfleury.cryptomoon.application.io.PortfolioSnapshotJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import java.math.BigDecimal

class PortfolioHistoryJson(
    val type: PortfolioValueType,
    val data: List<PortfolioSnapshotJson>
) {
    companion object {
        fun of(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?) = PortfolioHistoryJson(
            portfolioHistory.type,
            portfolioHistory.snapshots.map { PortfolioSnapshotJson.of(it, conversionRate) }
        )
    }
}