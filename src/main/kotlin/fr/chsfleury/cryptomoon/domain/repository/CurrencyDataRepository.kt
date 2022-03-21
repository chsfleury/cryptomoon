package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH
import fr.chsfleury.cryptomoon.domain.model.CurrencyData
import fr.chsfleury.cryptomoon.domain.model.Quote

interface CurrencyDataRepository {

    fun all(): Set<CurrencyData>

    fun save(currencyData: CurrencyData)

    fun saveATH(allTimeHigh: ATH)
    fun saveATH(aths: Collection<ATH>)

    fun saveQuote(quote: Quote)
    fun saveQuote(quotes: Collection<Quote>)

}