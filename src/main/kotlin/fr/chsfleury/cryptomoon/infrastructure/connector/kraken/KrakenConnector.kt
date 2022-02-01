package fr.chsfleury.cryptomoon.infrastructure.connector.kraken

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

class KrakenConnector(http: HttpClient, config: Map<String, Any>): Connector {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing kraken api key")
    private val secretKey = config["secretKey"] as? String ?: error("missing kraken secret key")
    private val client = ClientFactory.create(KrakenClient::class, "https://api.kraken.com", http)

    override val name = "KRAKEN"

    override fun get(knownCurrencies: Set<Currency>): AccountSnapshot {
        log.info("[{}] Account Snapshot", name)

        val nonce = System.currentTimeMillis()
        val signature = KrakenRequestHelper.getSignature(KrakenClient.USER_BALANCES_PATH, null, nonce, secretKey)
        val balances = client.userBalances(apiKey, signature, nonce)
            .result
            .asSequence()
            .map {
                val mappedCurrency = KrakenCurrencyMapper.map(it.key)
                val currency = Currencies[mappedCurrency]
                Balance(currency, BigDecimal(it.value)) }
            .filterBalances(knownCurrencies)

        return AccountSnapshot.of(name, Instant.now(), balances)
    }
}