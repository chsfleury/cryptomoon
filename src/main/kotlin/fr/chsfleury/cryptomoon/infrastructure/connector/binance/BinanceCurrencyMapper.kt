package fr.chsfleury.cryptomoon.infrastructure.connector.binance

object BinanceCurrencyMapper {
    private val trueLDCurrencies = emptyList<String>()
    private val currencyModifiers = mapOf(
        "SHIB2" to "SHIB"
    )

    fun map(currencyCode: String): String {
        val cleanCurrencyCode = if (currencyCode.startsWith("LD") && currencyCode !in trueLDCurrencies) {
            currencyCode.substring("LD".length)
        } else {
            currencyCode
        }
        return currencyModifiers[cleanCurrencyCode] ?: cleanCurrencyCode
    }
}