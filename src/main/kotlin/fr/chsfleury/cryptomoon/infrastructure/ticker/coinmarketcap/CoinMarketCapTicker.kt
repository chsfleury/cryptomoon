package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleQuote
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient

class CoinMarketCapTicker(http: HttpClient, config: Map<String, Any>): Ticker, Logging {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing coinmarketcap api key")
    private val client = ClientFactory.create(CoinMarketCapClient::class, "https://pro-api.coinmarketcap.com", http)

    override fun tickTopCurrencies(): List<Quote> {
        val params = CMCListingLatestParams()

        return client.listingLatest(apiKey, params)
            .data
            .map { SimpleQuote(Currencies[it.symbol], it.cmcRank, it.quote[Fiat.USD.name]?.price ?: error("error with fiat conversion")) }
            .toList()
    }

    override fun tickKnownCurrencies(knownCurrencies: Set<Currency>): List<Quote> {
        log.info("Tick for {}", knownCurrencies)
        val symbolParam = knownCurrencies.joinToString(",")
        val params = CMCLatestQuotesParams(symbolParam)

        return client.latestQuotes(apiKey, params)
            .data
            .map { (_, item) ->
                SimpleQuote(
                    Currencies[item.symbol],
                    item.cmcRank,
                    item.quote[Fiat.USD.name]?.price ?: error("error with fiat conversion")
                )
            }
            .toList()
    }
}