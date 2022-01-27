package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.Quotes

@JsonPropertyOrder("origin", "timestamp", "quotes")
data class QuotesJson(
    val origin: String,
    val timestamp: String,
    val quotes: List<QuoteJson>
) {
    companion object {
        fun of(quotes: Quotes)= QuotesJson(
            quotes.origin,
            quotes.timestamp.toString(),
            quotes.quoteList.map { QuoteJson(it.currency.symbol, it.rank, it.fiat, it.price) }
        )
    }
}