package fr.chsfleury.cryptomoon.domain.model

class PortfolioConfiguration(
    private val portfolios: Map<String, Set<String>>
) {
    fun names(): Set<String> = portfolios.keys
    operator fun get(name: String): Set<String>? = portfolios[name]
}