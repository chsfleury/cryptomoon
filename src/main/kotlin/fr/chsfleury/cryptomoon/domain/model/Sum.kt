package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class Sum(initialValue: BigDecimal = BigDecimal.ZERO) {
    private var value = initialValue

    operator fun plusAssign(v: BigDecimal) {
        value += v
    }

    fun value() = value
}