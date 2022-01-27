package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Quotes

interface QuoteRepository {

    fun insert(quotes: Quotes)

}