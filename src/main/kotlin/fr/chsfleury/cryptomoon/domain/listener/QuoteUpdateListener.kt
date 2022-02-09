package fr.chsfleury.cryptomoon.domain.listener

import fr.chsfleury.cryptomoon.domain.model.Currency

interface QuoteUpdateListener {
    fun onQuoteUpdate(currencies: Set<Currency>)
}