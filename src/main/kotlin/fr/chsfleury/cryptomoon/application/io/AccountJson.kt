package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal
import java.util.*
import kotlin.collections.LinkedHashMap

@JsonPropertyOrder("origin", "total", "assets", "timestamp")
class AccountJson(
    val origin: String,
    val total: BigDecimal,
    val assets: Map<String, AssetJson>,
    val timestamp: String
) {
    companion object {
        fun of(accountStats: AccountStats, conversionRate: BigDecimal?): AccountJson {
            val assetStats = accountStats
                .assetStats
                .sortedBy { it.currency.symbol }
                .associateTo(LinkedHashMap()) { it.currency.symbol to AssetJson.of(it, conversionRate) }
            return AccountJson(
                accountStats.origin,
                accountStats.total.applyRate(conversionRate).clean(),
                assetStats,
                accountStats.timestamp.toString()
            )
        }
    }
}