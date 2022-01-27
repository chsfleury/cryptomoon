package fr.chsfleury.cryptomoon.infrastructure.ticker

import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.CoinMarketCapTicker
import java.net.http.HttpClient

enum class Tickers(private val factory: (HttpClient, Map<String, Any>) -> Ticker) {
    COINMARKETCAP({ http, config -> CoinMarketCapTicker(http, config) });

    fun get(httpClient: HttpClient, config: Map<String, Any>): Ticker = factory(httpClient, config)
}