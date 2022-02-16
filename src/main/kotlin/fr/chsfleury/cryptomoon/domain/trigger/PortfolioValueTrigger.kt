package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers.COINMARKETCAP
import java.time.Duration
import java.time.Instant

class PortfolioValueTrigger(
    private val portfolioService: PortfolioService,
    private val quoteService: QuoteService,
    private val athService: ATHService,
    private val valueType: PortfolioValueType,
    triggerName: String,
    delay: Duration
) : Trigger(triggerName, delay) {

    override fun execute() {
        portfolioService.getPortfolioNames().forEach { name ->
            val portfolio = portfolioService.getPortfolio(name, true)
            val stats = portfolio.stats(quoteService, athService, COINMARKETCAP)
            val now = Instant.now()
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