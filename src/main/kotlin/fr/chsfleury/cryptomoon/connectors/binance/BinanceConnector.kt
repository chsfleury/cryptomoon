package fr.chsfleury.cryptomoon.connectors.binance

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.model.Balance
import fr.chsfleury.cryptomoon.model.BalanceReport
import fr.chsfleury.cryptomoon.model.CustomConnector
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.binance.BinanceExchange
import java.math.BigDecimal
import java.net.http.HttpClient
import java.net.http.HttpRequest

class BinanceConnector(http: HttpClient, config: ObjectNode) : CustomConnector(http) {
    companion object {
        const val ACCOUNT_URI ="https://api.binance.com/api/v3/account"
        const val BSWAP_URI = "https://api.binance.com/sapi/v1/bswap/liquidity"
    }

    private val apiKey = config["apiKey"]?.asText() ?: error("missing binance api key")
    private val secretKey = config["secretKey"]?.asText() ?: error("missing binance secret key")
    private val binance: Exchange

    init {
        val exchangeSpec = BinanceExchange().defaultExchangeSpecification
        exchangeSpec.apiKey = apiKey
        exchangeSpec.secretKey = secretKey
        binance = ExchangeFactory.INSTANCE.createExchange(exchangeSpec)
    }

    override fun decorateRequest(req: HttpRequest.Builder) {
        TODO("Not yet implemented")
    }

    override fun extract(): BalanceReport {
        val accountInfos = binance.accountService.accountInfo
        val balances = accountInfos.wallet.balances.asSequence()
            .filter { it.value.total.compareTo(BigDecimal.ZERO) != 0 }
            .map { entry ->
                Balance(entry.key.currencyCode, entry.value.total)
            }
            .toList()
        return BalanceReport.of(balances)
    }

//    private fun accountBalances(): List<Balance> {
//        UriBuilder.fromUri(ACCOUNT_URI)
//        val req = HttpRequest.newBuilder(ACCOUNT_URI).GET()
//        val response = send(req)
//        return if (response.statusCode() == 200) {
//            val stakingResponse: GetStakingsResponse = Json.readValue(response.body())
//            stakingResponse.data.map { contract ->
//                Balance(contract.currencyCode, contract.amount + contract.reward)
//            }
//        } else {
//            emptyList()
//        }
//    }

}