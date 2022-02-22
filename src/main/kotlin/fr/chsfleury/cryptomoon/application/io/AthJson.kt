package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal

data class AthJson(
    val symbol: String,
    val price: BigDecimal,
    val fiat: Fiat
) {
    companion object {
        fun of(ath: ATH): AthJson = AthJson(ath.currency.symbol, ath.price, Fiat.USD)
    }
}
