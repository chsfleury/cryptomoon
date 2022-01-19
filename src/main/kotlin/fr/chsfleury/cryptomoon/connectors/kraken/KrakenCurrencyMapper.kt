package fr.chsfleury.cryptomoon.connectors.kraken

object KrakenCurrencyMapper {
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
        "XETH" to "ETH",
        "ETH2.S" to "ETH",
        "KAVA.S" to "KAVA",
        "ADA.S" to "ADA",
        "SOL.S" to "SOL",
    )

    fun map(currencyCode: String): String = currencyModifiers[currencyCode] ?: currencyCode
}