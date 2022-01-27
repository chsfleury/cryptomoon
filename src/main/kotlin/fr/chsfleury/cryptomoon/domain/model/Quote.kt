package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class Quote(
    val currency: Currency,
    val rank: Int,
    val fiat: Fiat,
    val price: BigDecimal
)