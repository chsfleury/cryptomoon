package fr.chsfleury.cryptomoon.infrastructure.connector.justmining

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.filterBalances
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.utils.ClientFactory
import fr.chsfleury.cryptomoon.utils.logger
import java.net.http.HttpClient
import java.time.Instant

class JustMiningConnector(http: HttpClient, config: Map<String, Any>): Connector {
    private val log = logger()

    private val apiKey = config["apiKey"] as? String ?: error("missing just-mining api key")
    private val client = ClientFactory.create(JustMiningClient::class, "https://api.just-mining.com", http)

    override val name = "justmining"

    override fun get(knownCurrencies: Set<Currency>): AccountSnapshot {
        log.info("[{}] Account Snapshot", name)
        val stakingBalances = stakingBalances(knownCurrencies)
        val masternodeBalances = masternodeBalances(knownCurrencies)
        return AccountSnapshot.of(name, Instant.now(), stakingBalances, masternodeBalances)
    }

    private fun stakingBalances(knownCurrencies: Set<Currency>): List<Balance> {
        return client.stakingBalances(apiKey)
            .data
            .asSequence()
            .map { contract ->
                Balance(Currencies[contract.currencyCode], contract.amount + contract.reward)
            }
            .filterBalances(knownCurrencies)
    }

    private fun masternodeBalances(knownCurrencies: Set<Currency>): List<Balance> {
        return client.masternodeBalances(apiKey)
            .data
            .asSequence()
            .map { masternode ->
                Balance(Currencies[masternode.currencyCode], masternode.collateral + masternode.reward)
            }
            .filterBalances(knownCurrencies)
    }
}