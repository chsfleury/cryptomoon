package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.ticker.ATHTicker
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto.CoinListRequest
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto.CoinResponseItem
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient
import java.time.Instant

class LiveCoinWatchTicker(http: HttpClient, config: Map<String, Any>): ATHTicker, Logging {
    private val log = logger()

    override val name = "LIVECOINWATCH"

    private val apiKey = config["apiKey"] as? String ?: error("missing livecoinwatch api key")
    private val maxOffset = config["maxOffset"] as? Int ?: 500
    private val client = ClientFactory.create(LiveCoinWatchClient::class, "https://api.livecoinwatch.com", http)

    override fun tickAll(fiat: Fiat): Quotes {
        val request = CoinListRequest(fiat.name)
        val quoteList = client.coinList(request, apiKey)
            .mapIndexed { rank, item -> Quote(Currencies[item.code], rank + 1, fiat, item.rate) }
            .toList()

        return Quotes(name, quoteList, Instant.now())
    }

    override fun tickKnownCurrencies(knownCurrencies: Set<Currency>, fiat: Fiat): Quotes {
        log.info("[{}] Tick ({}) for {}", name, fiat, knownCurrencies)
        val quoteList = mutableListOf<Quote>()
        forEachKnownCurrencies(knownCurrencies, fiat) { items, offset ->
            val response = items.asSequence()
                .mapIndexed { rank, item -> Quote(Currencies[item.code], rank + 1 + offset, fiat, item.rate) }
                .filter { it.currency in knownCurrencies }
                .toList()
            quoteList.addAll(response)
        }
        return Quotes(name, quoteList, Instant.now())
    }

    override fun getATH(knownCurrencies: Set<Currency>): Set<ATH> {
        log.info("[{}] ATH Tick ({}) for {}", name, Fiat.USD, knownCurrencies)
        val athSet = mutableSetOf<ATH>()
        forEachKnownCurrencies(knownCurrencies, Fiat.USD) { items, _ ->
            items.asSequence()
                .map { item -> ATH(Currencies[item.code], item.allTimeHighUSD) }
                .filter { it.currency in knownCurrencies }
                .forEach(athSet::add)
        }
        return athSet
    }

    private fun forEachKnownCurrencies(knownCurrencies: Set<Currency>, fiat: Fiat, responseHandler: (List<CoinResponseItem>, Int) -> Unit) {
        val remainingCurrencies = knownCurrencies.toMutableSet()
        var offset = 0
        while (offset < maxOffset && remainingCurrencies.isNotEmpty()) {
            val request = CoinListRequest(fiat.name, offset)
            responseHandler(client.coinList(request, apiKey), offset)
            offset += 100
        }
    }
}