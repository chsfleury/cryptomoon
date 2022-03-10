package fr.chsfleury.cryptomoon.infrastructure.connector.kraken

object KrakenCurrencyMapper {
    private val trueDotSCurrencies = emptyList<String>()

    private val currencyModifiers = mapOf(
        "XTZ.S" to "XTZ",
        "XZEC" to "ZEC",
        "XXRP" to "XRP",
        "ETH2" to "ETH",
        "XXBT" to "BTC",
        "ATOM.S" to "ATOM",
        "DOT.S" to "DOT",
        "FLOW.S" to "FLOW",
        "KSM.S" to "KSM",
        "ZEUR" to "EUR",
        "XLTC" to "LTC",
        "XETH" to "ETH"
    )

    fun map(currencyCode: String): String {
        val cleanCurrencyCode = if (currencyCode.endsWith(".S") && currencyCode !in trueDotSCurrencies) {
            currencyCode.dropLast(2)
        } else {
            currencyCode
        }
        return currencyModifiers[cleanCurrencyCode] ?: cleanCurrencyCode
    }
}