package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.Quotes
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository

class QuoteService(
    private val quoteRepository: QuoteRepository
) {
    fun insert(quotes: Quotes) = quoteRepository.insert(quotes)
}