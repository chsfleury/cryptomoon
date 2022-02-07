package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class CoinResponseItem (
    val name: String,
    val code: String,
    val allTimeHighUSD: BigDecimal,
    val rate: BigDecimal
)