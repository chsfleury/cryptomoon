package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.utils.BigDecimals.eq
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioStats (
    val name: String,
    val totalUSD: BigDecimal,
    val athTotalUSD: BigDecimal,
    val accountStats: Set<AccountStats>,
    val mergedAccountStats: AccountStats
) {
    val ratioAth: BigDecimal = if (athTotalUSD eq BigDecimal.ZERO) {
        BigDecimal.ZERO
    } else {
        totalUSD
            .multiply(BigDecimal(100))
            .divide(athTotalUSD, RoundingMode.HALF_EVEN)
            .setScale(2, RoundingMode.HALF_UP)
    }

    val athMultiplier: BigDecimal = if (athTotalUSD eq BigDecimal.ZERO) {
        BigDecimal.ZERO
    } else {
        athTotalUSD.divide(totalUSD, RoundingMode.HALF_EVEN)
            .setScale(1, RoundingMode.HALF_UP)
    }
}