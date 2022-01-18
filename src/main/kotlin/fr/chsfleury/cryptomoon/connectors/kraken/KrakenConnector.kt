package fr.chsfleury.cryptomoon.connectors.kraken

import com.fasterxml.jackson.databind.node.ObjectNode
import fr.chsfleury.cryptomoon.model.ApiConnector
import fr.chsfleury.cryptomoon.model.BalanceReport

class KrakenConnector(config: ObjectNode): ApiConnector {
    override fun extract(): BalanceReport {
        return BalanceReport.of()
    }
}