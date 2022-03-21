package fr.chsfleury.cryptomoon.domain.model.impl

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Currency
import java.math.BigDecimal

class SimpleATH(
    override val currency: Currency,
    override val athUSD: BigDecimal
): ATH {
    override fun toString() = "ATH($currency: $athUSD)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleATH

        if (currency != other.currency) return false

        return true
    }

    override fun hashCode(): Int {
        return currency.hashCode()
    }
}