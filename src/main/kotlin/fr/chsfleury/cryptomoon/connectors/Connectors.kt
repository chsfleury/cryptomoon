package fr.chsfleury.cryptomoon.connectors

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.connectors.justmining.JustMiningConnector
import fr.chsfleury.cryptomoon.connectors.binance.BinanceConnector
import fr.chsfleury.cryptomoon.connectors.kraken.KrakenConnector
import fr.chsfleury.cryptomoon.model.ApiConnector
import java.net.http.HttpClient

enum class Connectors(private val factory: (HttpClient, ObjectNode) -> ApiConnector) {
    JUSTMINING({ http, config -> JustMiningConnector(http, config) }),
    BINANCE({ http, config -> BinanceConnector(http, config) }),
    KRAKEN({ http, config -> KrakenConnector(http, config) });

    fun get(httpClient: HttpClient, config: ObjectNode): ApiConnector = factory(httpClient, config)
}