package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Portfolio
import fr.chsfleury.cryptomoon.domain.model.PortfolioConfiguration
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository

class PortfolioService(
    portfolioRepository: PortfolioRepository,
    connectorService: ConnectorService,
    private val accountService: AccountService
) {
    private val portfolioConfiguration: PortfolioConfiguration

    init {
        portfolioConfiguration = portfolioRepository.loadConfiguration() ?: defaultPortfolioConfiguration(connectorService)
    }

    fun getPorfolio(name: String, merged: Boolean): Portfolio {
        val portfolioAccountNames = portfolioConfiguration[name] ?: error("unknown portfolio")
        val accounts = if (merged) {
            setOf(accountService.mergedAccounts(portfolioAccountNames))
        } else {
            accountService.allAccounts()
        }
        val filteredAccounts = accounts.asSequence().filter { it.origin == AccountSnapshot.ALL || (it.origin.uppercase() in portfolioAccountNames) }.toSet()
        return Portfolio(name, filteredAccounts)
    }

    fun getPorfolioAccount(portfolioName: String, origin: String): AccountSnapshot? {
        val portfolioCfg = portfolioConfiguration[portfolioName] ?: error("unknown portfolio")
        return if (origin.uppercase() in portfolioCfg) {
            accountService.getAccount(origin)
        } else {
            null
        }
    }

    fun getPortfolioNames(): Set<String> {
        return portfolioConfiguration.names()
    }

    fun getPorfolioAccountNames(portfolioName: String): Set<String> {
        return portfolioConfiguration[portfolioName] ?: error("unknown portfolio")
    }

    private fun defaultPortfolioConfiguration(connectorService: ConnectorService): PortfolioConfiguration {
        val map = mapOf("main" to connectorService.names())
        return PortfolioConfiguration(map)
    }
}