package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.utils.FiatMap

class PortfolioStats (
    val name: String,
    val total: FiatMap,
    val accountStats: Set<AccountStats>,
)