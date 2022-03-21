package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch

class CoinSingleRequest(
    val code: String,
    val meta: Boolean
) {
    val currency = "USD"
}

class CoinListRequest(
    val offset: Int = 0,
    val limit: Int = 100,
    val sort: String = "rank",
    val order: String = "ascending",
    val meta: Boolean = true
) {
    val currency = "USD"
}