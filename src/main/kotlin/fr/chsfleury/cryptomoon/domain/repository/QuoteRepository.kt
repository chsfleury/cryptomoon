package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Quote

interface QuoteRepository {

    fun getLatestQuotes(): List<Quote>
    fun save(quotes: List<Quote>)

}