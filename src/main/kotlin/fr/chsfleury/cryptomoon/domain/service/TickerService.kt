package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.repository.TickerRepository
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers

class TickerService(
    private val tickerRepository: TickerRepository
) {
    private val tickers: Map<String, Ticker>

    init {
        val tickerConfiguration = tickerRepository.loadConfiguration() ?: error("cannot load tickers")
        val tickerMap = mutableMapOf<String, Ticker>()
        tickerConfiguration.tickers.forEach { cfg ->
            tickerRepository.createTicker(cfg)?.also { tickerMap[it.name] = it }
        }

        tickers = tickerMap
    }

    operator fun get(name: String): Ticker? = tickers[name]
    operator fun get(ticker: Tickers): Ticker = tickers[ticker.name] ?: error("ticker not already implemented")

    fun names(): Set<String> = tickers.keys

    fun forEach(action: (Ticker) -> Unit) {
        tickers.values.forEach(action)
    }
}