package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

@JsonPropertyOrder("balance", "price", "value")
class AssetJson (
    val balance: BigDecimal,
    val price: FiatMap,
    val value: FiatMap
) {
    companion object {
        fun of(assetStats: AssetStats) = AssetJson(
            assetStats.balance.stripTrailingZeros(),
            assetStats.price.clean(),
            assetStats.value.clean()
        )
    }
}