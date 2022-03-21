package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

@JsonPropertyOrder("name", "total", "athTotal", "accounts")
class PortfolioJson(
    val name: String,
    val total: BigDecimal,
    val athTotal: BigDecimal,
    val accounts: List<AccountJson>
) {
    companion object {
        fun of(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): PortfolioJson = PortfolioJson(
            portfolioStats.name,
            portfolioStats.totalUSD.applyRate(conversionRate).clean(),
            portfolioStats.athTotalUSD.applyRate(conversionRate).clean(),
            portfolioStats.accountStats.asSequence()
                .sortedBy(AccountStats::origin)
                .map { AccountJson.of(it, conversionRate) }
                .toList()
        )
    }
}