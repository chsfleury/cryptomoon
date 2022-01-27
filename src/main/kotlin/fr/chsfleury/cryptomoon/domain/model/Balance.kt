package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class Balance (
    val currency: Currency,
    val amount: BigDecimal
) {
    fun isZero() = amount.compareTo(BigDecimal.ZERO) == 0

    companion object {
        fun Sequence<Balance>.filterBalances(knownCurrencies: Set<Currency>) = filter { !it.isZero() || it.currency in knownCurrencies }.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Balance

        if (currency != other.currency) return false

        return true
    }

    override fun hashCode(): Int {
        return currency.hashCode()
    }

    override fun toString(): String {
        return "Balance(currency=$currency, amount=$amount)"
    }


}