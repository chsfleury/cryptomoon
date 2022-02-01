package fr.chsfleury.cryptomoon.application.io

import java.math.BigDecimal
import java.time.Instant

class FiatPairJson(
    val usdToEur: BigDecimal,
    val at: String
) {
    companion object {
        fun of(pair: Pair<Instant, BigDecimal>): FiatPairJson = FiatPairJson(pair.second.stripTrailingZeros(), pair.first.toString())
    }
}