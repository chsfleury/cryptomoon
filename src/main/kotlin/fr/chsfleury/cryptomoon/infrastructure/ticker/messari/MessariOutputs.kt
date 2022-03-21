package fr.chsfleury.cryptomoon.infrastructure.ticker.messari

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

interface MessariAsset {
    val symbol: String?
    val athUSD: BigDecimal?
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessariResponse<T: Any>(
    val data: T
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllAssetsItem(
    override val symbol: String?,
    val metrics: AllAssetsMetrics?
): MessariAsset {
    override val athUSD: BigDecimal? = metrics?.allTimeHigh?.priceUSD
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllAssetsMetrics(
    @JsonProperty("all_time_high") val allTimeHigh: AssetATH?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AssetATH(
    @JsonProperty("price") val priceUSD: BigDecimal?
)