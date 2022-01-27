package fr.chsfleury.cryptomoon.infrastructure.repository.file

import fr.chsfleury.cryptomoon.domain.model.PortfolioConfiguration
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration

object LocalFilePortfolioRepository: PortfolioRepository {
    override fun loadConfiguration(): PortfolioConfiguration? {
        val configPortfolioMap = LocalFileConfiguration.root["portfolios"] as? Map<*, *>
        return configPortfolioMap?.let { map ->
            val portfolioMap: MutableMap<String, Set<String>> = mutableMapOf()
            map.forEach { portfolioMap[it.key as String] = (it.value as Collection<String>).toSet() }
            PortfolioConfiguration(portfolioMap)
        }
    }
}