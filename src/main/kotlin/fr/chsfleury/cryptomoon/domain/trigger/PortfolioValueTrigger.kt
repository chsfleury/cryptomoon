package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import java.time.Duration
import java.time.Instant

class PortfolioValueTrigger(
    private val portfolioService: PortfolioService,
    private val currencyService: CurrencyService,
    private val valueType: PortfolioValueType,
    triggerName: String,
    delay: Duration
) : Trigger(triggerName, delay) {

    override fun execute() {
        portfolioService.getPortfolioNames().forEach { name ->
            val portfolio = portfolioService.getPortfolio(name, true)
            val stats = portfolio.stats(currencyService.marketData())
            val now = Instant.now()
            valueType.findValue(stats)
                ?.let { PorfolioValueSnapshot(now, it) }
                ?.run {
                    portfolioService.saveSnapshot(name, valueType, this)
                }
        }
    }

}