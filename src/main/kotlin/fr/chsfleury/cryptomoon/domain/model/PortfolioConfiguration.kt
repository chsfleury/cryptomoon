package fr.chsfleury.cryptomoon.domain.model

class PortfolioConfiguration(
    private val portfolios: Map<String, Set<String>>
) {
    fun names(): Set<String> = portfolios.keys
    operator fun get(name: String): Set<String>? = portfolios[name]

    fun portfolioForAccount(accounts: Collection<String>): Set<String> {
        return accounts.asSequence()
            .flatMap { accountName ->
                portfolios.asSequence()
                    .filter { accountName in it.value }
                    .map { it.key }
            }
            .toSet()
    }
}