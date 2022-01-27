package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Quote
import fr.chsfleury.cryptomoon.domain.model.Quotes
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCListingLatestParams
import fr.chsfleury.cryptomoon.utils.ClientFactory
import java.net.http.HttpClient
import java.time.Instant

class CoinMarketCapTicker(http: HttpClient, config: Map<String, Any>): Ticker {
    override val name = "coinmarketcap"

    private val apiKey = config["apiKey"] as? String ?: error("missing coinmarketcap api key")
    private val client = ClientFactory.create(CoinMarketCapClient::class, "https://pro-api.coinmarketcap.com", http)

    override fun tick(fiat: Fiat): Quotes {
        val params = CMCListingLatestParams(fiat.name)
        val quoteList = client.listingLatest(apiKey, params)
            .data
            .map { Quote(Currencies[it.symbol], it.cmcRank, fiat, it.quote[fiat.name]?.price ?: error("error with fiat conversion")) }
            .toList()

        return Quotes(name, quoteList, Instant.now())
    }
}