package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class AssetStats (
    val currency: Currency,
    val rank: Int?,
    val balance: BigDecimal,
    val price: FiatMap,
    val value: FiatMap,
    val ath: FiatMap,
    val athRatio: Double?
)