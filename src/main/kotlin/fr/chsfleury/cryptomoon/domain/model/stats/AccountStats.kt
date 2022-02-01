package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.utils.FiatMap
import java.time.Instant

class AccountStats (
    val origin: String,
    val total: FiatMap,
    val assetStats: Set<AssetStats>,
    val timestamp: Instant
)