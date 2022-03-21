package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import fr.chsfleury.cryptomoon.domain.ticker.FiatTicker
import java.time.Duration

class FiatPairTrigger(
    private val fiatTicker: FiatTicker,
    private val currencyService: CurrencyService
): Trigger("fiatPair", Duration.ofDays(1)) {

    override fun execute() {
        val fiatPairs = fiatTicker.tick(Fiat.EUR)
        currencyService.saveRate(fiatPairs)
    }

}