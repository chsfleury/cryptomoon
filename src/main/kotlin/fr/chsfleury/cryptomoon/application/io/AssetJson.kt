package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats
import java.math.BigDecimal

@JsonPropertyOrder("balance", "price", "value")
class AssetJson (
    val balance: BigDecimal,
    val price: BigDecimal,
    val value: BigDecimal
) {
    companion object {
        fun of(assetStats: AssetStats, conversionRate: BigDecimal?) = AssetJson(
            assetStats.balance.stripTrailingZeros(),
            assetStats.priceUSD.applyRate(conversionRate).clean(),
            assetStats.valueUSD.applyRate(conversionRate).clean()
        )
    }
}