package fr.chsfleury.cryptomoon.domain.model

enum class Currencies(
    val symbol: String,
    val stablecoin: Boolean = false,
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

    companion object {
        private const val LIVECOINWATCH_LOGOS_URL = "https://lcw.nyc3.cdn.digitaloceanspaces.com/production/currencies/64/%s.png"

        operator fun get(symbol: String): Currency {
            return values()
                .firstOrNull { it.symbol == symbol.uppercase() }
                ?.let { Currency(it.symbol, it.fiat, it.stablecoin, "/assets/currencies/${it.symbol}.png") }
                ?: Currency(symbol, false, false, LIVECOINWATCH_LOGOS_URL.format(symbol.lowercase()))
        }
    }
}