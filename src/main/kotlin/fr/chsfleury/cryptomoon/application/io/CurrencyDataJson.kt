package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal

@JsonPropertyOrder("symbol", "price", "ath", "rank")
data class CurrencyDataJson(
    val symbol: String,
    val price: BigDecimal,
    val ath: BigDecimal,
    val rank: Int
)
