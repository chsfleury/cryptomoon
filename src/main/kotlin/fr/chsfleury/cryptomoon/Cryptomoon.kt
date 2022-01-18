package fr.chsfleury.cryptomoon

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.yaml
import fr.chsfleury.cryptomoon.configuration.Conf
import fr.chsfleury.cryptomoon.connectors.Connectors
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
            .get("/") { ctx -> ctx.result("UP")}
            .start(config[Conf.port])

        val http = HttpClient.newHttpClient()
        val connectors = connectorConfiguration.get("connectors") as ArrayNode
        connectors.forEach {
            val node = it as ObjectNode
            val connectorType = node["type"]?.asText()?.uppercase() ?: error("bad connector type")
            val connector = Connectors.valueOf(connectorType).get(http, node)

            javalin.get("/${connectorType.lowercase()}") { ctx ->
                val balanceReport = connector.extract()
                ctx.json(balanceReport)
            }
            log.info("adding {} connector", connectorType.lowercase())
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