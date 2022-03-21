package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import fr.chsfleury.cryptomoon.domain.ticker.AthTicker
import java.time.Duration

class ATHTrigger(
    private val athTicker: AthTicker,
    private val athTickerBackup: AthTicker,
    private val currencyService: CurrencyService,
    private val accountService: AccountService
): Trigger("ath", Duration.ofDays(1)) {

    override fun execute() {
        val (aths, remaining) = athTicker.tickKnownCurrencies(accountService.getKnownCurrencies())
        currencyService.saveATH(aths)

        if (remaining.isNotEmpty()) {
            val (athsWithBackup, _) = athTickerBackup.tickKnownCurrencies(remaining)
            currencyService.saveATH(athsWithBackup)
        }

    }

}