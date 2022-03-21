package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinSingleResponseItem (
    val name: String,
    val allTimeHighUSD: BigDecimal?,
    val rate: BigDecimal?
) {
    fun toCoinResponseItem(code: String): CoinResponseItem = CoinResponseItem(name, code, allTimeHighUSD, rate)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinResponseItem (
    val name: String,
    val code: String,
    val allTimeHighUSD: BigDecimal?,
    val rate: BigDecimal?
)