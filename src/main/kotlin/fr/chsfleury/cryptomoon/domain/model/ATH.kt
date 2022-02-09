package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class ATH(
    val currency: Currency,
    val price: BigDecimal
) {
    override fun toString() = "ATH($currency: $price)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ATH

        if (currency != other.currency) return false

        return true
    }

    override fun hashCode(): Int {
        return currency.hashCode()
    }
}