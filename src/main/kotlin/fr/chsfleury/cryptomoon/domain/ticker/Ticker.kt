package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.Quote

interface Ticker {
    fun tickTopCurrencies(): List<Quote>
    fun tickKnownCurrencies(knownCurrencies: Set<Currency>): List<Quote>
}