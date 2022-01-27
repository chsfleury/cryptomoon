package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCQuote(
    val price: BigDecimal,
    @JsonProperty("last_updated") val lastUpdated: String
)
