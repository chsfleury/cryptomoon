package fr.chsfleury.cryptomoon.connectors.binance

object BinanceCurrencyMapper {
    private val currencyModifiers = mapOf(
        "LDAAVE" to "AAVE",
        "LDBTC" to "BTC",
        "LDBUSD" to "BUSD",
        "LDDOGE" to "DOGE",
        "LDFTM" to "FTM",
        "LDSHIB2" to "SHIB",
        "LDUSDT" to "USDT",
        "LDKAVA" to "KAVA"
    )

    fun map(currencyCode: String): String = currencyModifiers[currencyCode] ?: currencyCode
}