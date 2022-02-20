package fr.chsfleury.cryptomoon.infrastructure.connector.binance

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.filterBalances
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.logger
import java.math.BigDecimal
import java.net.http.HttpClient
import java.time.Instant

class BinanceConnector(http: HttpClient, config: Map<String, Any>) : Connector {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing binance api key")
    private val secretKey = config["secretKey"] as? String ?: error("missing binance secret key")
    private val client = ClientFactory.create(BinanceClient::class, "https://api.binance.com", http)

    override val name = "BINANCE"

    override fun get(knownCurrencies: Set<Currency>): AccountSnapshot {
        log.info("[{}] Account Snapshot", name)
        val accountBalances = accountBalances(knownCurrencies)
        val liquidityPoolBalances = liquidityPoolBalances(knownCurrencies)

        val movedBalances = knownCurrencies.asSequence()
            .filter { curr ->
                accountBalances.none { it.currency == curr } && liquidityPoolBalances.none { it.currency == curr }
            }
            .map { Balance(it, BigDecimal.ZERO) }
            .toList()

        return AccountSnapshot.of(name, Instant.now(), accountBalances, liquidityPoolBalances, movedBalances)
    }

    private fun accountBalances(knownCurrencies: Set<Currency>): List<Balance> {
        val queryMap: LinkedHashMap<String, Any> = LinkedHashMap()
        queryMap["recvWindow"] = 5000L
        BinanceRequestHelper.signRequest(queryMap, secretKey)
        return client.accountData(apiKey, queryMap)
            .balances
            .asSequence()
            .map {
                val mappedCurrency = BinanceCurrencyMapper.map(it.currency)
                val currency = Currencies[mappedCurrency]
                Balance(currency, it.free + it.locked)
            }
            .filterBalances(knownCurrencies)
    }

    private fun liquidityPoolBalances(knownCurrencies: Set<Currency>): List<Balance> {
        val queryMap: LinkedHashMap<String, Any> = LinkedHashMap()
        queryMap["recvWindow"] = 5000L
        BinanceRequestHelper.signRequest(queryMap, secretKey)
        return client.liquidityPoolData(apiKey, queryMap)
            .asSequence()
            .flatMap { poolData ->
                poolData.share.asset.asSequence().map {
                    val mappedCurrency = BinanceCurrencyMapper.map(it.key)
                    val currency = Currencies[mappedCurrency]
                    Balance(currency, BigDecimal(it.value))
                }
            }
            .filterBalances(knownCurrencies)
    }

}