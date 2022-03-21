package fr.chsfleury.cryptomoon.utils

import java.math.BigDecimal

object BigDecimals {
    private val EPSILON: BigDecimal = BigDecimal.valueOf(0.000001)

    infix fun BigDecimal.eq(other: BigDecimal): Boolean {
        return minus(other).abs() <= EPSILON
    }

}