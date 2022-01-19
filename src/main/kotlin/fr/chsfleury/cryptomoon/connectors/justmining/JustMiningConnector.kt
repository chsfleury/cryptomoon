package fr.chsfleury.cryptomoon.connectors.justmining

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.model.ApiConnector
import fr.chsfleury.cryptomoon.model.Balance
import fr.chsfleury.cryptomoon.model.BalanceReport
import fr.chsfleury.cryptomoon.utils.ClientFactory
import java.net.http.HttpClient

class JustMiningConnector(http: HttpClient, config: ObjectNode): ApiConnector {
    private val apiKey = config["apiKey"]?.asText() ?: error("missing just-mining api key")
    private val client = ClientFactory.create(JustMiningClient::class, "https://api.just-mining.com", http)

    override val name = "justmining"

    override fun report(): BalanceReport {
        val stakingBalances = stakingBalances()
        val masternodeBalances = masternodeBalances()
        return BalanceReport.of(stakingBalances, masternodeBalances)
    }

    private fun stakingBalances(): List<Balance> {
        return client.stakingBalances(apiKey)
            .data
            .map { contract ->
                Balance(contract.currencyCode, contract.amount + contract.reward)
            }
    }

    private fun masternodeBalances(): List<Balance> {
        return client.masternodeBalances(apiKey)
            .data
            .map { masternode ->
                Balance(masternode.currencyCode, masternode.collateral + masternode.reward)
            }
    }
}