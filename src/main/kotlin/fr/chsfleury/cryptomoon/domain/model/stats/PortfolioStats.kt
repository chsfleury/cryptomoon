package fr.chsfleury.cryptomoon.domain.model.stats

import fr.chsfleury.cryptomoon.domain.model.Fiat.USD
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioStats (
    val name: String,
    val total: FiatMap,
    val athTotal: FiatMap,
    val accountStats: Set<AccountStats>,
    val mergedAccountStats: AccountStats
) {
    val ratioAth: BigDecimal = athTotal[USD]
        ?.to(total[USD] ?: BigDecimal.ZERO)
        ?.let { it.second.multiply(BigDecimal(100)) / it.first }
        ?.setScale(2, RoundingMode.HALF_UP)
        ?: BigDecimal.ZERO

    val athMultiplier: BigDecimal = total[USD]
        ?.to(athTotal[USD] ?: BigDecimal.ZERO)
        ?.let { it.second / it.first }
        ?.setScale(1, RoundingMode.HALF_UP)
        ?: BigDecimal.ZERO
}