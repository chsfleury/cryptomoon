package fr.chsfleury.cryptomoon

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.yaml
import fr.chsfleury.cryptomoon.configuration.Conf
import fr.chsfleury.cryptomoon.connectors.Connectors
import fr.chsfleury.cryptomoon.io.BalanceReportJson
import fr.chsfleury.cryptomoon.model.ApiConnector
import fr.chsfleury.cryptomoon.model.BalanceReport
import fr.chsfleury.cryptomoon.utils.Json
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import io.javalin.Javalin
import java.net.http.HttpClient
import java.nio.file.Files
import java.nio.file.Paths

object Cryptomoon: Logging {
    val log = logger()

    @JvmStatic
    fun main(args: Array<String>) {
        val config = config(Conf)

        val apiConnectorFilePath = Paths.get(config[Conf.apiConfigFile])
        val apiConnectorFileContent = Files.readAllBytes(apiConnectorFilePath)
        val connectorConfiguration = Json.readTree(apiConnectorFileContent)

        val javalin = Javalin.create()
            .start(config[Conf.port])

        val http = HttpClient.newHttpClient()
        val connectorConfigurations = connectorConfiguration.get("connectors") as ArrayNode
        val connectors = connectorConfigurations.map {
            val node = it as ObjectNode
            val connectorType = node["type"]?.asText()?.uppercase() ?: error("bad connector type")
            Connectors.valueOf(connectorType).get(http, node)
        }

        connectors.forEach { connector ->
            javalin.get("/${connector.name}") { ctx ->
                val report = connector.report()
                ctx.json(BalanceReportJson(report.balanceMap, report.timestamp))
            }
            log.info("adding {} connector", connector.name)
        }

        javalin.get("/") { ctx ->
            val report = BalanceReport.merge(connectors.map(ApiConnector::report))
            ctx.json(BalanceReportJson(report.balanceMap, report.timestamp))
        }
    }

    private fun config(vararg specs: ConfigSpec) = config {
        specs.forEach { addSpec(it) }
    }

    private fun config(init: Config.() -> Unit) = Config(init)
        .apply {
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        }
        .from.yaml.resource("cryptomoon.yml", true)
        .from.yaml.file("cryptomoon.yml", true)
        .from.env()
        .from.systemProperties()

}