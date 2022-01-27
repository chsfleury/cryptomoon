package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Quotes

interface Ticker {
    val name: String
    fun tick(fiat: Fiat): Quotes
}