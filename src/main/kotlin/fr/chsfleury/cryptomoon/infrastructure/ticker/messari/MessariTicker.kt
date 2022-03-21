package fr.chsfleury.cryptomoon.infrastructure.ticker.messari

import com.google.common.util.concurrent.RateLimiter
import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH
import fr.chsfleury.cryptomoon.domain.ticker.AthTicker
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient

class MessariTicker(http: HttpClient, config: Map<String, Any>) : AthTicker {
    companion object : Logging {
        private val log = logger()
        private const val ALL_ASSETS_FIELDS = "symbol,metrics/all_time_high/price"
        private const val PAGE_SIZE = 500
    }

    private val apiKey = config["apiKey"] as? String ?: error("missing messari api key")
    private val maxPage = config["maxPage"] as? Int ?: 20
    private val rateLimiter = RateLimiter.create(29.0/60)
    private val client = ClientFactory.create(MessariClient::class, "https://data.messari.io/", http)

    override fun tickKnownCurrencies(knownCurrencies: Set<Currency>): Pair<Set<ATH>, Set<Currency>> {
        log.info("ATH Tick for {}", knownCurrencies)
        val allTimeHighs = mutableSetOf<ATH>()
        val remainingCurrencies = forEachKnownCurrencies(knownCurrencies) { items ->
            items.asSequence()
                .filter { it.symbol != null && it.athUSD != null }
                .map { SimpleATH(Currencies[it.symbol!!], it.athUSD!!) }
                .filter { it.currency in knownCurrencies }
                .forEach { allTimeHighs.add(it) }
        }
        return allTimeHighs to remainingCurrencies
    }

    private fun forEachKnownCurrencies(knownCurrencies: Set<Currency>, responseHandler: (List<MessariAsset>) -> Unit): Set<Currency> {
        val remainingCurrencies = knownCurrencies.toMutableSet()
        var page = 1
        while (page <= maxPage && remainingCurrencies.isNotEmpty()) {
            val params = AllAssetsParams(ALL_ASSETS_FIELDS, PAGE_SIZE, page)
            val response = allAssets(params)
            responseHandler(response)
            response.asSequence()
                .filter { it.symbol != null && it.athUSD != null }
                .map { Currencies[it.symbol!!] }
                .forEach(remainingCurrencies::remove)

            page++
        }
        return remainingCurrencies
    }

    private fun allAssets(params: AllAssetsParams): List<AllAssetsItem> {
        rateLimiter.acquire()
        return client.allAssets(apiKey, params).data
    }
}