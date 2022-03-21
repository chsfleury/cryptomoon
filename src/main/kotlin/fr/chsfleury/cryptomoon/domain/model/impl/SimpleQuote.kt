package fr.chsfleury.cryptomoon.domain.model.impl

import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.Quote
import java.math.BigDecimal

class SimpleQuote(
    override val currency: Currency,
    override val rank: Int,
    override val priceUSD: BigDecimal
): Quote