package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Quotes

interface Ticker {
    val name: String
    fun tickAll(fiat: Fiat): Quotes
    fun tickKnownCurrencies(knownCurrencies: Set<Currency>, fiat: Fiat): Quotes
}