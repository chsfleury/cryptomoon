package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

interface Quote: CurrencyBased {

    val priceUSD: BigDecimal
    val rank: Int

}