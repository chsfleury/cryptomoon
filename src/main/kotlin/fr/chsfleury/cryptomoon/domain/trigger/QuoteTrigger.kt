package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.domain.service.TickerService
import java.time.Duration

class QuoteTrigger(
    private val tickerService: TickerService,
    private val quoteService: QuoteService,
    private val accountService: AccountService,
    private val fiat: Fiat,
    triggerName: String,
    delay: Duration = Duration.ofMinutes(15L),
    after: List<Trigger> = emptyList()
): Trigger(triggerName, delay, after) {

    override fun execute() {
        tickerService.forEach {
            quoteService.insert(it.tickKnownCurrencies(accountService.getKnownCurrencies(), fiat))
        }
    }

}