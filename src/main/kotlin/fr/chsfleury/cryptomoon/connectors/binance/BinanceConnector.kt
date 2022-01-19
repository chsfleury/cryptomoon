package fr.chsfleury.cryptomoon.connectors.binance

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.model.ApiConnector
import fr.chsfleury.cryptomoon.model.Balance
import fr.chsfleury.cryptomoon.model.BalanceReport
import fr.chsfleury.cryptomoon.model.CustomConnector
import fr.chsfleury.cryptomoon.utils.ClientFactory
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.binance.BinanceExchange
import java.math.BigDecimal
import java.net.http.HttpClient
import java.net.http.HttpRequest

class BinanceConnector(http: HttpClient, config: ObjectNode) : ApiConnector {
    private val apiKey = config["apiKey"]?.asText() ?: error("missing binance api key")
    private val secretKey = config["secretKey"]?.asText() ?: error("missing binance secret key")
    private val client = ClientFactory.create(BinanceClient::class, "https://api.binance.com", http)

    override val name = "binance"

    override fun report(): BalanceReport {
        val accountBalances = accountBalances()
        val liquidityPoolBalances = liquidityPoolBalances()
        return BalanceReport.of(accountBalances, liquidityPoolBalances)
    }

    private fun accountBalances(): List<Balance> {
        val queryMap: LinkedHashMap<String, Any> = LinkedHashMap()
        queryMap["recvWindow"] = 5000L
        BinanceRequestHelper.signRequest(queryMap, secretKey)
        return client.accountData(apiKey, queryMap)
            .balances
            .asSequence()
            .map {
                Balance(BinanceCurrencyMapper.map(it.currency), it.free + it.locked)
            }
            .filterNot(Balance::isZero)
            .toList()
    }

    private fun liquidityPoolBalances(): List<Balance> {
        val queryMap: LinkedHashMap<String, Any> = LinkedHashMap()
        queryMap["recvWindow"] = 5000L
        BinanceRequestHelper.signRequest(queryMap, secretKey)
        return client.liquidityPoolData(apiKey, queryMap)
            .asSequence()
            .flatMap { poolData ->
                poolData.share.asset.asSequence()
                    .map { Balance(BinanceCurrencyMapper.map(it.key), BigDecimal(it.value)) }
            }
            .filterNot(Balance::isZero)
            .toList()
    }

    private fun stakingBalances(): List<Balance> {
        val queryMap: LinkedHashMap<String, Any> = LinkedHashMap()
//        queryMap["type"] = "CUSTOMIZED_FIXED"
//        queryMap["type"] = "ACTIVITY"
//        queryMap["asset"] = "LUNA"
        queryMap["recvWindow"] = 5000L
        BinanceRequestHelper.signRequest(queryMap, secretKey)
        val response = client.stakingData(apiKey, queryMap)
        return emptyList()
    }

}