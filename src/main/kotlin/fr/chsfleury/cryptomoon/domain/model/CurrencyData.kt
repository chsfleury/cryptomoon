package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class CurrencyData(
    override val currency: Currency,
    override val priceUSD: BigDecimal,
    override val athUSD: BigDecimal,
    override val rank: Int
): Quote, ATH