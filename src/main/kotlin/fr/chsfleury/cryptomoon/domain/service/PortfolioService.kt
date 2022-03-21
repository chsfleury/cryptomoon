package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.listener.AccountUpdateListener
import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.repository.PortfolioHistoryRepository
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

class PortfolioService(
    portfolioRepository: PortfolioRepository,
    private val portfolioHistoryRepository: PortfolioHistoryRepository,
    connectorService: ConnectorService,
    private val accountService: AccountService
): AccountUpdateListener, Logging {
    private val log = logger()

    private val portfolioConfiguration: PortfolioConfiguration
    private val portfolioCache = mutableMapOf<Pair<String, Boolean>, Portfolio>()

    init {
        portfolioConfiguration = portfolioRepository.loadConfiguration() ?: defaultPortfolioConfiguration(connectorService)
    }

    fun getPortfolios(merged: Boolean): Set<Portfolio> {
        return getPortfolioNames().mapTo(mutableSetOf()) { getPortfolio(it, merged) }
    }

    fun getPortfolio(name: String, merged: Boolean): Portfolio {
        return portfolioCache.computeIfAbsent(name to merged) { (n, m) ->
            log.debug("retrieving {} portfolio data", name)
            val portfolioAccountNames = portfolioConfiguration[n] ?: error("unknown portfolio")
            val accounts = if (m) {
                setOf(accountService.mergedAccounts(portfolioAccountNames))
            } else {
                accountService.allAccounts()
            }
            val filteredAccounts = accounts.asSequence()
                .filter { it.origin == AccountSnapshot.ALL || (it.origin in portfolioAccountNames) || compositeNameBelongToPortfolio(it.origin, portfolioAccountNames) }
                .toSet()
            Portfolio(n, filteredAccounts)
        }
    }

    fun getPortfolioAccount(portfolioName: String, origin: String): AccountSnapshot? {
        val portfolioCfg = portfolioConfiguration[portfolioName] ?: error("unknown portfolio")
        return if (origin in portfolioCfg) {
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
    
    fun getHistory(portfolioName: String, portfolioValueType: PortfolioValueType, days: Int): PortfolioHistory {
        return portfolioHistoryRepository.findBy(portfolioName, portfolioValueType, days)
    }

    fun saveSnapshot(portfolioName: String, portfolioValueType: PortfolioValueType, porfolioValueSnapshot: PorfolioValueSnapshot) {
        portfolioHistoryRepository.insert(portfolioName, portfolioValueType, porfolioValueSnapshot)
    }

    private fun defaultPortfolioConfiguration(connectorService: ConnectorService): PortfolioConfiguration {
        val map = mapOf("main" to connectorService.names())
        return PortfolioConfiguration(map)
    }

    private fun compositeNameBelongToPortfolio(accountSnapshotName: String, portfolioAccountNames: Collection<String>) = accountSnapshotName
            .splitToSequence('~')
            .all(portfolioAccountNames::contains)

    override fun onAccountUpdate(accounts: Collection<String>) {
        val obsoletePortfolios = portfolioConfiguration.portfolioForAccount(accounts)
        log.debug("remove cache for portfolio: {}", obsoletePortfolios)

        portfolioCache.keys
            .filter { it.first in obsoletePortfolios }
            .forEach { portfolioCache.remove(it) }
    }
}