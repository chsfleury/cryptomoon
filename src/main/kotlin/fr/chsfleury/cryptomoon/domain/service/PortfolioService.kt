package fr.chsfleury.cryptomoon.domain.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.repository.PortfolioHistoryRepository
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.time.Duration

class PortfolioService(
    portfolioRepository: PortfolioRepository,
    private val portfolioHistoryRepository: PortfolioHistoryRepository,
    connectorService: ConnectorService,
    private val accountService: AccountService
): Logging {
    private val log = logger()

    private val portfolioConfiguration: PortfolioConfiguration
    private val portfolioCache: LoadingCache<Pair<String, Boolean>, Portfolio> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .refreshAfterWrite(Duration.ofMinutes(3))
        .build { (name, merged) -> computePortfolio(name, merged) }

    init {
        portfolioConfiguration = portfolioRepository.loadConfiguration() ?: defaultPortfolioConfiguration(connectorService)
    }

    fun getPortfolios(merged: Boolean): Set<Portfolio> {
        return getPortfolioNames().mapTo(mutableSetOf()) { getPortfolio(it, merged) }
    }

    fun getPortfolio(name: String, merged: Boolean): Portfolio {
        return portfolioCache.get(name to merged)
    }

    private fun computePortfolio(name: String, merged: Boolean): Portfolio {
        log.debug("retrieving {} portfolio data", name)
        val portfolioAccountNames = portfolioConfiguration[name] ?: error("unknown portfolio")
        val accounts = if (merged) {
            setOf(accountService.mergedAccounts(portfolioAccountNames))
        } else {
            accountService.allAccounts()
        }
        val filteredAccounts = accounts.asSequence()
            .filter {
                it.origin == AccountSnapshot.ALL || (it.origin in portfolioAccountNames) || compositeNameBelongToPortfolio(
                    it.origin,
                    portfolioAccountNames
                )
            }
            .toSet()
        return Portfolio(name, filteredAccounts)
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
}