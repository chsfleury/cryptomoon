package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.repository.ConnectorRepository

class ConnectorService(
    connectorRepository: ConnectorRepository
) {
    private val connectors: Map<String, Connector>

    init {
        val connectorConfiguration = connectorRepository.loadConfiguration() ?: error("cannot load connectors")
        val connectorMap = mutableMapOf<String, Connector>()
        connectorConfiguration.connectors.forEach { cfg ->
            connectorRepository.createConnector(cfg)?.also { connectorMap[it.name] = it }
        }

        connectors = connectorMap
    }

    operator fun get(name: String): Connector? = connectors[name]
    fun names(): Set<String> = connectors.keys

    fun forEach(action: (Connector) -> Unit) {
        connectors.values.forEach(action)
    }
}