package fr.chsfleury.cryptomoon.domain.model

import java.time.Instant

class Quotes(
    val origin: String,
    val quoteList: List<Quote>,
    val timestamp: Instant
)