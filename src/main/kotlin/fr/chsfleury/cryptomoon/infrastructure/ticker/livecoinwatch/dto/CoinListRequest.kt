package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto

class CoinListRequest(
    val currency: String,
    val offset: Int = 0,
    val limit: Int = 100,
    val sort: String = "rank",
    val order: String = "ascending",
    val meta: Boolean = true
)