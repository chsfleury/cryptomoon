package fr.chsfleury.cryptomoon.domain.model

import java.time.Instant

class Quotes(
    val origin: String,
    val quoteList: List<Quote>,
    val timestamp: Instant
) {
    private val quoteMap by lazy { quoteList.associateBy { it.currency } }

    operator fun get(currency: Currency): Quote? = quoteMap[currency]
}