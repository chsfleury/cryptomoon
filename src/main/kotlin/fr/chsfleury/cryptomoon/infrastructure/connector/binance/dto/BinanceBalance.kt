package fr.chsfleury.cryptomoon.infrastructure.connector.binance.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class BinanceBalance(
    @JsonProperty("asset") val currency: String,
    @JsonProperty("free") val free: BigDecimal,
    @JsonProperty("locked") val locked: BigDecimal
)