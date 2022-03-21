package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class DeltaBalance(
    currency: Currency,
    amount: BigDecimal,
    val valueUSD: BigDecimal
): Balance(currency, amount) {
    override fun toString(): String {
        return "DeltaBalance(currency=$currency, amount=$amount, valueUSD=$valueUSD)"
    }
}