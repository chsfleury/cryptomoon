package fr.chsfleury.cryptomoon.infrastructure.ticker.abstractapi

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.ticker.FiatTicker
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.FiatMap
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient

class AbstractApiFiatTicker(http: HttpClient, config: Map<String, Any>): FiatTicker, Logging {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing abstract api key")
    private val client = ClientFactory.create(AbstractApiClient::class, "https://exchange-rates.abstractapi.com", http)

    override fun tick(vararg fiats: Fiat): FiatMap {
        val response = client.rates(RateParams(apiKey, fiats.joinToString("")))
        val result = FiatMap()
        response.rates.forEach { (f, r) -> result[Fiat[f]] = r }
        return result
    }
}