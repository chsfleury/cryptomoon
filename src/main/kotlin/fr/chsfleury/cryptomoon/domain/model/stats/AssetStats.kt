package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.domain.model.Currency
import java.math.BigDecimal

class AssetStats (
    val currency: Currency,
    val rank: Int?,
    val balance: BigDecimal,
    val priceUSD: BigDecimal,
    val valueUSD: BigDecimal,
    val athUSD: BigDecimal,
    val athRatio: Double?
) {
    override fun toString(): String {
        return "AssetStats(currency=$currency, rank=$rank, balance=$balance, price=$priceUSD, value=$valueUSD)"
    }
}