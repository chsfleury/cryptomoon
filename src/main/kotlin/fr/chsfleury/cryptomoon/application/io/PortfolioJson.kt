package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.utils.FiatMap

@JsonPropertyOrder("name", "total", "accounts")
class PortfolioJson(
    val name: String,
    val total: FiatMap,
    val accounts: List<AccountJson>
) {
    companion object {
        fun of(portfolioStats: PortfolioStats): PortfolioJson = PortfolioJson(
            portfolioStats.name,
            portfolioStats.total.clean(),
            portfolioStats.accountStats.asSequence()
                .sortedBy(AccountStats::origin)
                .map { AccountJson.of(it) }
                .toList()
        )
    }
}