package fr.chsfleury.cryptomoon.domain.connector

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.utils.Logging

interface Connector: Logging {
    val name: String
    fun get(knownCurrencies: Set<Currency> = emptySet()): AccountSnapshot
}