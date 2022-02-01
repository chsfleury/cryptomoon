package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Quotes

interface QuoteRepository {

    fun getLatestQuotes(ticker: String): Quotes?
    fun insert(quotes: Quotes)

}