package fr.chsfleury.cryptomoon.connectors.kraken

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.connectors.binance.BinanceClient
import fr.chsfleury.cryptomoon.model.ApiConnector
import fr.chsfleury.cryptomoon.model.Balance
import fr.chsfleury.cryptomoon.model.BalanceReport
import fr.chsfleury.cryptomoon.utils.ClientFactory
import java.math.BigDecimal
import java.net.http.HttpClient

class KrakenConnector(http: HttpClient, config: ObjectNode): ApiConnector {

    private val apiKey = config["apiKey"]?.asText() ?: error("missing kraken api key")
    private val secretKey = config["secretKey"]?.asText() ?: error("missing kraken secret key")
    private val client = ClientFactory.create(KrakenClient::class, "https://api.kraken.com", http)

    override val name = "kraken"

    override fun report(): BalanceReport {
        val nonce = System.currentTimeMillis()
        val signature = KrakenRequestHelper.getSignature(KrakenClient.USER_BALANCES_PATH, null, nonce, secretKey)
        val balances = client.userBalances(apiKey, signature, nonce)
            .result
            .asSequence()
            .map { Balance(KrakenCurrencyMapper.map(it.key), BigDecimal(it.value)) }
            .filterNot { it.isZero() }
            .toList()
        return BalanceReport.of(balances)
    }
}