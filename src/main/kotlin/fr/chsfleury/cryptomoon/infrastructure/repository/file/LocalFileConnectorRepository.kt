package fr.chsfleury.cryptomoon.infrastructure.repository.file

import fr.chsfleury.cryptomoon.domain.connector.Connector
import fr.chsfleury.cryptomoon.domain.model.ConnectorConfiguration
import fr.chsfleury.cryptomoon.domain.repository.ConnectorRepository
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration
import fr.chsfleury.cryptomoon.infrastructure.connector.Connectors
import fr.chsfleury.cryptomoon.infrastructure.http.Http
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

object LocalFileConnectorRepository: ConnectorRepository, Logging {
    private val log = logger()

    override fun loadConfiguration(): ConnectorConfiguration? {
        val configConnectorList = LocalFileConfiguration.root["connectors"] as? List<*>
        return configConnectorList?.let { list ->
            val connectorList: MutableList<Map<String, Any>> = mutableListOf()
            list.forEach {
                (it as? Map<String, Any>)?.also(connectorList::add)
            }
            ConnectorConfiguration(connectorList)
        }
    }

    override fun createConnector(config: Map<String, Any>): Connector? {
        val type = config["type"] as? String ?: error("bad connector type")
        return try {
            Connectors.valueOf(type.uppercase()).get(Http.client, config)
        } catch (t: Throwable) {
            log.error("error while creating connector: {}", type)
            null
        }
    }
}