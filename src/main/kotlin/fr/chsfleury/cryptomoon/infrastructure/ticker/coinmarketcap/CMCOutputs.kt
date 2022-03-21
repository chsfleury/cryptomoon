package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCResponse<T: Any>(
    val data: T
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCQuote(
    val price: BigDecimal,
    @JsonProperty("last_updated") val lastUpdated: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCQuoteItem(
    val symbol: String,
    @JsonProperty("cmc_rank") val cmcRank: Int,
    val quote: Map<String, CMCQuote>
)

