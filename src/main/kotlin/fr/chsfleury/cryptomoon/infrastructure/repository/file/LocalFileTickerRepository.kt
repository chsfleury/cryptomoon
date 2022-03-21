package fr.chsfleury.cryptomoon.infrastructure.repository.file

import fr.chsfleury.cryptomoon.domain.model.TickerConfiguration
import fr.chsfleury.cryptomoon.domain.repository.TickerRepository
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration
import fr.chsfleury.cryptomoon.infrastructure.http.Http
import fr.chsfleury.cryptomoon.infrastructure.ticker.abstractapi.AbstractApiFiatTicker
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.CoinMarketCapTicker
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.LiveCoinWatchTicker
import fr.chsfleury.cryptomoon.infrastructure.ticker.messari.MessariTicker
import fr.chsfleury.cryptomoon.utils.Logging

object LocalFileTickerRepository: TickerRepository, Logging {

    override val configuration: TickerConfiguration by lazy {
        val tickerConfig = LocalFileConfiguration.root["coinmarketcap"] as? Map<String, Any> ?: error("cannot load ticker config")
        val ticker = CoinMarketCapTicker(Http.client, tickerConfig)

        val athTickerConfig = LocalFileConfiguration.root["messari"] as? Map<String, Any> ?: error("cannot load ath ticker config")
        val athTicker = MessariTicker(Http.client, athTickerConfig)

        val athTickerBackupConfig = LocalFileConfiguration.root["livecoinwatch"] as? Map<String, Any> ?: error("cannot load ath ticker config")
        val athTickerBackup = LiveCoinWatchTicker(Http.client, athTickerBackupConfig)

        val fiatTickerConfig = LocalFileConfiguration.root["abstractapi"] as? Map<String, Any> ?: error("cannot load fiat ticker config")
        val fiatTicker = AbstractApiFiatTicker(Http.client, fiatTickerConfig)

        TickerConfiguration(ticker, athTicker, athTickerBackup, fiatTicker)
    }
}