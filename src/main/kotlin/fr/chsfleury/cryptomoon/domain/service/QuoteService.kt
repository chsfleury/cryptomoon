package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.Quotes
import fr.chsfleury.cryptomoon.domain.repository.FiatPairRepository
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.max
import kotlin.math.min

class QuoteService(
    private val quoteRepository: QuoteRepository,
    private val fiatPairRepository: FiatPairRepository,
) {
    private val lastQuotes = mutableMapOf<String, Quotes>()
    private val lastFiatPairs = mutableListOf<Pair<Instant, BigDecimal>>()

    init {
        Tickers.values().forEach { ticker ->
            getLatestQuotes(ticker.name)?.also { lastQuotes[ticker.name] = it }
        }

        loadFiatPairs()
    }

    operator fun get(ticker: String) = lastQuotes[ticker]
    operator fun get(ticker: Tickers) = get(ticker.name)

    fun usdToEur(): BigDecimal? {
        checkLatestFiatPair()
        return if (lastFiatPairs.isNotEmpty()) {
            lastFiatPairs[0].second
        } else {
            null
        }
    }

    fun usdToEur(lastDays: Int): List<Pair<Instant, BigDecimal>> {
        checkLatestFiatPair()
        return lastFiatPairs.subList(0, min(lastFiatPairs.size, lastDays))
    }

    private fun checkLatestFiatPair() {
        if (lastFiatPairs.isEmpty() || !LocalDate.ofInstant(lastFiatPairs[0].first, ZoneOffset.UTC).equals(LocalDate.now())) {
            loadFiatPairs()
        }
    }

    private fun loadFiatPairs() {
        lastFiatPairs.clear()
        fiatPairRepository.getUsdToEuros()
            .sortedByDescending { it.first }
            .forEach(lastFiatPairs::add)
    }

    fun getLatestQuotes(ticker: String): Quotes? = quoteRepository.getLatestQuotes(ticker)

    fun insert(quotes: Quotes) {
        quoteRepository.insert(quotes)

        val latestQuotes = lastQuotes[quotes.origin]
        if (latestQuotes == null || latestQuotes.timestamp.isBefore(quotes.timestamp)) {
            lastQuotes[quotes.origin] = quotes
        }
    }
}