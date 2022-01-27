package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Portfolio

@JsonPropertyOrder("name", "accounts")
class PortfolioJson(
    val name: String,
    val accounts: List<AccountJson>
) {
    companion object {
        fun of(portfolio: Portfolio): PortfolioJson = PortfolioJson(
            portfolio.name,
            portfolio.accounts.asSequence().sortedBy(AccountSnapshot::origin).map { AccountJson.of(it) }.toList()
        )
    }
}