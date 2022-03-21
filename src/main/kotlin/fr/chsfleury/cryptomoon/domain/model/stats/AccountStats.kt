package fr.chsfleury.cryptomoon.domain.model.stats

import java.math.BigDecimal
import java.time.Instant

class AccountStats (
    val origin: String,
    val total: BigDecimal,
    val assetStats: Set<AssetStats>,
    val timestamp: Instant
) {
    val assetsByValueDesc: List<AssetStats>
        get() = assetStats.sortedByDescending { it.valueUSD }

    override fun toString(): String {
        return "AccountStats(origin='$origin', total=$total, timestamp=$timestamp)"
    }
}