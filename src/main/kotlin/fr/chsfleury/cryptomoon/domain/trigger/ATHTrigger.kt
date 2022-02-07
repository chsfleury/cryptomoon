package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.service.ATHService
import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.ticker.ATHTicker
import java.time.Duration

class ATHTrigger(
    private val athTicker: ATHTicker,
    private val athService: ATHService,
    private val accountService: AccountService
): Trigger("ath", Duration.ofDays(1)) {

    override fun execute() {
        val aths = athTicker.getATH(accountService.getKnownCurrencies())
        athService.save(aths)
    }

}