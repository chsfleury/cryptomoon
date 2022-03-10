package fr.chsfleury.cryptomoon.domain.model

enum class Currencies(
    val symbol: String,
    stablecoin: Boolean = false,
    val fiat: Boolean = false
) {
    ADA("ADA"),
    BETH("BETH"),
    BTC("BTC"),
    DAI("DAI", true),
    DOT("DOT"),
    ETH("ETH"),
    GLMR("GLMR"),
    KAVA("KAVA"),
    ROSE("ROSE"),
    SOL("SOL"),
    USDT("USDT", true),
    USDC("USDC", true),

    // Fiats
    EUR("EUR", fiat = true),
    USD("USD", fiat = true);

    val currency = Currency(symbol, fiat, stablecoin, "/assets/currencies/$symbol.png")

    companion object {
        private const val LIVECOINWATCH_LOGOS_URL = "https://lcw.nyc3.cdn.digitaloceanspaces.com/production/currencies/64/%s.png"

        operator fun get(symbol: String): Currency {
            return values()
                .firstOrNull { it.symbol == symbol.uppercase() }
                ?.let(Currencies::currency)
                ?: Currency(symbol, false, false, LIVECOINWATCH_LOGOS_URL.format(symbol.lowercase()))
        }
    }
}