package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import java.time.Duration

class QuoteTrigger(
    private val ticker: Ticker,
    private val currencyService: CurrencyService,
    private val accountService: AccountService,
    after: List<Trigger> = emptyList()
): Trigger("quotes", Duration.ofMinutes(15L), after) {

    override fun execute() {
        ticker.run {
            currencyService.saveQuote(tickKnownCurrencies(accountService.getKnownCurrencies()))
        }
    }

}