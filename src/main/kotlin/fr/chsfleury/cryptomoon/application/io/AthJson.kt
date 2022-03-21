package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH
import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal

data class AthJson(
    val symbol: String,
    val price: BigDecimal,
    val fiat: Fiat
) {
    companion object {
        fun of(ath: SimpleATH): AthJson = AthJson(ath.currency.symbol, ath.athUSD, Fiat.USD)
    }
}
