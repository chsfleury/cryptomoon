package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.TickerConfiguration
import fr.chsfleury.cryptomoon.domain.ticker.Ticker

interface TickerRepository {

    fun loadConfiguration(): TickerConfiguration?
    fun createTicker(config: Map<String, Any>): Ticker?

}