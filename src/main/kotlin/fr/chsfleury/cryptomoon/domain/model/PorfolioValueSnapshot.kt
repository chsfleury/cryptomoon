package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import java.math.BigDecimal
import java.time.Instant

class PorfolioValueSnapshot(
    val at: Instant,
    val valueUSD: BigDecimal
) {
    fun applyRate(conversionRate: BigDecimal?): PorfolioValueSnapshot = if (conversionRate == null) {
        this
    } else {
        PorfolioValueSnapshot(at, valueUSD.applyRate(conversionRate))
    }

    override fun toString(): String = valueUSD.toPlainString() + " USD @ " + at.toString()
}