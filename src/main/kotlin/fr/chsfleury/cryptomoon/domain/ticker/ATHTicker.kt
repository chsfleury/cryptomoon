package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Currency

interface ATHTicker: Ticker {

    fun getATH(knownCurrencies: Set<Currency>): Set<ATH>

}