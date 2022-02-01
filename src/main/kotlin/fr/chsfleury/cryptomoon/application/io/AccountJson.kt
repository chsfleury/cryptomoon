package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.util.*
import kotlin.collections.LinkedHashMap

@JsonPropertyOrder("origin", "total", "assets", "timestamp")
class AccountJson(
    val origin: String,
    val total: FiatMap,
    val assets: Map<String, AssetJson>,
    val timestamp: String
) {
    companion object {
        fun of(accountStats: AccountStats): AccountJson {
            val assetStats = accountStats
                .assetStats
                .sortedBy { it.currency.symbol }
                .associateTo(LinkedHashMap()) { it.currency.symbol to AssetJson.of(it) }
            return AccountJson(
                accountStats.origin,
                accountStats.total.clean(),
                assetStats,
                accountStats.timestamp.toString()
            )
        }
    }
}