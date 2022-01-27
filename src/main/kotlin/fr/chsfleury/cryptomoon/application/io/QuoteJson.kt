package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal

data class QuoteJson(
    val symbol: String,
    val rank: Int,
    val fiat: Fiat,
    val price: BigDecimal
)
