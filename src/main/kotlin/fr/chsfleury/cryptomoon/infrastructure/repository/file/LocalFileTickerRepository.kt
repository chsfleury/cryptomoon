package fr.chsfleury.cryptomoon.infrastructure.repository.file

import fr.chsfleury.cryptomoon.domain.model.TickerConfiguration
import fr.chsfleury.cryptomoon.domain.repository.TickerRepository
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration
import fr.chsfleury.cryptomoon.infrastructure.http.Http
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

object LocalFileTickerRepository: TickerRepository, Logging {
    private val log = logger()

    override fun loadConfiguration(): TickerConfiguration? {
        val configTickerList = LocalFileConfiguration.root["tickers"] as? List<*>
        return configTickerList?.let { list ->
            val tickerList: MutableList<Map<String, Any>> = mutableListOf()
            list.forEach {
                (it as? Map<String, Any>)?.also(tickerList::add)
            }
            TickerConfiguration(tickerList)
        }
    }

    override fun createTicker(config: Map<String, Any>): Ticker? {
        val type = config["type"] as? String ?: error("bad ticker type")
        return try {
            Tickers.valueOf(type.uppercase()).get(Http.client, config)
        } catch (t: Throwable) {
            log.error("error while creating connector: {}", type)
            null
        }
    }
}