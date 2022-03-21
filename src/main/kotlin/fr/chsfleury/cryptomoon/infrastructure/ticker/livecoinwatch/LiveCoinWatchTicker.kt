package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH
import fr.chsfleury.cryptomoon.domain.ticker.AthTicker
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient

class LiveCoinWatchTicker(http: HttpClient, config: Map<String, Any>): AthTicker, Logging {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing livecoinwatch api key")
    private val client = ClientFactory.create(LiveCoinWatchClient::class, "https://api.livecoinwatch.com", http)

    override fun tickKnownCurrencies(knownCurrencies: Set<Currency>): Pair<Set<ATH>, Set<Currency>> {
        log.info("ATH Tick for {}", knownCurrencies)
        val allTimeHighs = mutableSetOf<ATH>()
        val remainingCurrencies = forEachKnownCurrencies(knownCurrencies) { items ->
            items.asSequence()
                .filter { it.allTimeHighUSD != null }
                .map { SimpleATH(Currencies[it.code], it.allTimeHighUSD!!) }
                .filter { it.currency in knownCurrencies }
                .forEach { allTimeHighs.add(it) }
        }
        return allTimeHighs to remainingCurrencies
    }

    private fun forEachKnownCurrencies(knownCurrencies: Set<Currency>, responseHandler: (List<CoinResponseItem>) -> Unit): Set<Currency> {
        val remainingCurrencies = mutableSetOf<Currency>()
        knownCurrencies.forEach { currency ->
            try {
                val request = CoinSingleRequest(currency.symbol, true)
                val response = client.coinSingle(request, apiKey)
                responseHandler(listOf(response.toCoinResponseItem(currency.symbol)))
            } catch (ex: Exception) {
                log.error("Cannot request info for currency $currency", ex)
                remainingCurrencies.add(currency)
            }
        }
        return remainingCurrencies
    }

}