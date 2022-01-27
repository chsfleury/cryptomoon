package fr.chsfleury.cryptomoon.domain.model

object Currencies {
    private val STABLE_COINS = setOf("USDT", "USDC", "DAI")
    private val FIATS = Fiat.values().map(Fiat::name).toSet()

    operator fun get(symbol: String) = Currency(symbol, symbol in FIATS, symbol in STABLE_COINS)
}