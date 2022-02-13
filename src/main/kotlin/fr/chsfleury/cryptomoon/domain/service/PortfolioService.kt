package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.listener.AccountUpdateListener
import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.repository.PortfolioHistoryRepository
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.math.BigDecimal

class PortfolioService(
    portfolioRepository: PortfolioRepository,
    private val portfolioHistoryRepository: PortfolioHistoryRepository,
    connectorService: ConnectorService,
    private val quoteService: QuoteService,
    private val athService: ATHService,
    private val accountService: AccountService
): AccountUpdateListener, Logging {
    private val log = logger()

    private val portfolioConfiguration: PortfolioConfiguration
    private val portfolioCache = mutableMapOf<Pair<String, Boolean>, Portfolio>()
    private val portfolioStatsCache = mutableMapOf<Pair<Portfolio, Tickers>, PortfolioStats>()

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

    fun computeStats(portfolio: Portfolio, ticker: Tickers): PortfolioStats {
        return portfolioStatsCache.computeIfAbsent(portfolio to ticker) {
            log.debug("computing {} portfolio stats", portfolio.name)
            val total = FiatMap()
            val accountStatsSet = portfolio.accounts.mapTo(mutableSetOf()) { accountService.computeStats(it, ticker) }
            accountStatsSet.forEach { total += it.total }
            val athTotalInUSD = accountStatsSet.asSequence()
                .flatMap(AccountStats::assetStats)
                .map { assetStats -> assetStats.balance * (athService[assetStats.currency] ?: BigDecimal.ZERO) }
                .sumOf { it }
            val athFiatMap = FiatMap()
            athFiatMap[Fiat.USD] = athTotalInUSD
            quoteService.usdToEur()?.also { usdToEur ->
                athFiatMap[Fiat.EUR] = athTotalInUSD * usdToEur
            }
            PortfolioStats(portfolio.name, total, athFiatMap, accountStatsSet)
        }
    }
    
    fun getHistory(portfolioName: String, portfolioValueType: PortfolioValueType, fiat: Fiat, days: Int): PortfolioHistory {
        return portfolioHistoryRepository.findBy(portfolioName, portfolioValueType, fiat, days)
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

        portfolioStatsCache.keys
            .filter { it.first.name in obsoletePortfolios }
            .forEach { portfolioStatsCache.remove(it) }
    }
}