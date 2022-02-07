package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Portfolio
import fr.chsfleury.cryptomoon.domain.model.PortfolioConfiguration
import fr.chsfleury.cryptomoon.domain.model.stats.AccountStats
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.domain.repository.PortfolioRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class PortfolioService(
    portfolioRepository: PortfolioRepository,
    connectorService: ConnectorService,
    private val quoteService: QuoteService,
    private val athService: ATHService,
    private val accountService: AccountService
) {
    private val portfolioConfiguration: PortfolioConfiguration

    init {
        portfolioConfiguration = portfolioRepository.loadConfiguration() ?: defaultPortfolioConfiguration(connectorService)
    }

    fun getPortfolios(merged: Boolean): Set<Portfolio> {
        return getPortfolioNames().mapTo(mutableSetOf()) { getPortfolio(it, merged) }
    }

    fun getPortfolio(name: String, merged: Boolean): Portfolio {
        val portfolioAccountNames = portfolioConfiguration[name] ?: error("unknown portfolio")
        val accounts = if (merged) {
            setOf(accountService.mergedAccounts(portfolioAccountNames))
        } else {
            accountService.allAccounts()
        }
        val filteredAccounts = accounts.asSequence()
            .filter { it.origin == AccountSnapshot.ALL || (it.origin in portfolioAccountNames) || compositeNameBelongToPortfolio(it.origin, portfolioAccountNames) }
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

    fun computeStats(portfolio: Portfolio, ticker: Tickers): PortfolioStats {
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

        return PortfolioStats(portfolio.name, total, athFiatMap, accountStatsSet)
    }

    private fun defaultPortfolioConfiguration(connectorService: ConnectorService): PortfolioConfiguration {
        val map = mapOf("main" to connectorService.names())
        return PortfolioConfiguration(map)
    }

    private fun compositeNameBelongToPortfolio(accountSnapshotName: String, portfolioAccountNames: Collection<String>) = accountSnapshotName
            .splitToSequence('~')
            .all(portfolioAccountNames::contains)
}