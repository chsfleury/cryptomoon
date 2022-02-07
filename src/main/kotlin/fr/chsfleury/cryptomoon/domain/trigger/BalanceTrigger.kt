package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.ConnectorService
import java.time.Duration

class BalanceTrigger(
    private val connectorService: ConnectorService,
    private val accountService: AccountService,
): Trigger("balance", Duration.ofHours(8)) {

    override fun execute() {
        val knownCurrencies = accountService.getKnownCurrencies()
        connectorService.forEach {
            accountService.insert(it.get(knownCurrencies))
        }
    }

}