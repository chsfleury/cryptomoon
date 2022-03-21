package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

data class CMCLatestQuotesParams(
    val symbol: String,
    val skip_invalid: Boolean = true
) {
    val convert = "USD"
}

data class CMCListingLatestParams(
    val start: Int = 1,
    val limit: Int = 200
) {
    val convert = "USD"
}