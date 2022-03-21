package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.ticker.AthTicker
import fr.chsfleury.cryptomoon.domain.ticker.FiatTicker
import fr.chsfleury.cryptomoon.domain.ticker.Ticker

class TickerConfiguration(
    val ticker: Ticker,
    val athTicker: AthTicker,
    val athTickerBackup: AthTicker,
    val fiatTicker: FiatTicker
)