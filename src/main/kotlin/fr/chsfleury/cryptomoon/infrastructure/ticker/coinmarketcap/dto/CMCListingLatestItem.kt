package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCListingLatestItem(
    val symbol: String,
    @JsonProperty("cmc_rank") val cmcRank: Int,
    val quote: Map<String, CMCQuote>
)
