package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import java.time.Duration
import java.time.Instant

class PortfolioValueTrigger(
    private val portfolioService: PortfolioService
) : Trigger("portfolioValue", Duration.ofHours(1)) {

    override fun execute() {
        portfolioService.getPortfolioNames().forEach { name ->
            val portfolio = portfolioService.getPortfolio(name, true)
            val stats = portfolioService.computeStats(portfolio, Tickers.COINMARKETCAP)
            val now = Instant.now()
            PortfolioValueType.values().forEach { valueType ->
                Fiat.values().forEach { fiat ->
                    valueType.findValue(stats, fiat)
                        ?.let { PorfolioValueSnapshot(fiat, now, it) }
                        ?.run {
                            portfolioService.saveSnapshot(name, valueType, this)
                        }
                }
            }
        }
    }

}