package fr.chsfleury.cryptomoon.infrastructure.connector

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.infrastructure.connector.binance.BinanceConnector
import fr.chsfleury.cryptomoon.infrastructure.connector.justmining.JustMiningConnector
import fr.chsfleury.cryptomoon.infrastructure.connector.kraken.KrakenConnector
import java.net.http.HttpClient

enum class Connectors(private val factory: (HttpClient, Map<String, Any>) -> Connector) {
    JUSTMINING({ http, config -> JustMiningConnector(http, config) }),
    BINANCE({ http, config -> BinanceConnector(http, config) }),
    KRAKEN({ http, config -> KrakenConnector(http, config) });

    fun get(httpClient: HttpClient, config: Map<String, Any>): Connector = factory(httpClient, config)
}