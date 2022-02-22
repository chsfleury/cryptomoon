package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto

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