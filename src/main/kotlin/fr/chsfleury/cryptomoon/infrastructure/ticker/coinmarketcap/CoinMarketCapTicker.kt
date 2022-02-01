package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCLatestQuotesParams
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCListingLatestParams
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient
import java.time.Instant

class CoinMarketCapTicker(http: HttpClient, config: Map<String, Any>): Ticker, Logging {
    private val log = logger()

    override val name = "COINMARKETCAP"

    private val apiKey = config["apiKey"] as? String ?: error("missing coinmarketcap api key")
    private val client = ClientFactory.create(CoinMarketCapClient::class, "https://pro-api.coinmarketcap.com", http)

    override fun tickAll(fiat: Fiat): Quotes {
        val params = CMCListingLatestParams(fiat.name)
        val quoteList = client.listingLatest(apiKey, params)
            .data
            .map { Quote(Currencies[it.symbol], it.cmcRank, fiat, it.quote[fiat.name]?.price ?: error("error with fiat conversion")) }
            .toList()

        return Quotes(name, quoteList, Instant.now())
    }

    override fun tickKnownCurrencies(knownCurrencies: Set<Currency>, fiat: Fiat): Quotes {
        log.info("[{}] Tick ({}) for {}", name, fiat, knownCurrencies)
        val symbolParam = knownCurrencies.joinToString(",")
        val params = CMCLatestQuotesParams(symbolParam, fiat.name, true)
        val quoteList = client.latestQuotes(apiKey, params)
            .data
            .map { (_, item) -> Quote(Currencies[item.symbol], item.cmcRank, fiat, item.quote[fiat.name]?.price ?: error("error with fiat conversion")) }
            .toList()

        return Quotes(name, quoteList, Instant.now())
    }
}