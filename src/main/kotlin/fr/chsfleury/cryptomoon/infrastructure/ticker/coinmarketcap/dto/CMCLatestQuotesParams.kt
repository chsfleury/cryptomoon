package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto

data class CMCLatestQuotesParams(
    val symbol: String,
    val convert: String,
    val skip_invalid: Boolean
)