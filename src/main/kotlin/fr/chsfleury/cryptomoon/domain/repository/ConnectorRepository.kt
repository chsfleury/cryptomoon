package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.model.ConnectorConfiguration

interface ConnectorRepository {

    fun loadConfiguration(): ConnectorConfiguration?
    fun createConnector(config: Map<String, Any>): Connector?

}