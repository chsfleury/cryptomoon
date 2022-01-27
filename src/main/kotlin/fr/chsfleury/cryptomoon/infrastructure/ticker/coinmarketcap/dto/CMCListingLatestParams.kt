package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto

data class CMCListingLatestParams(
    val convert: String,
    val start: Int = 1,
    val limit: Int = 200
)