package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH
import fr.chsfleury.cryptomoon.domain.model.Currency

interface AthTicker {
    fun tickKnownCurrencies(knownCurrencies: Set<Currency>): Pair<Set<ATH>, Set<Currency>>
}