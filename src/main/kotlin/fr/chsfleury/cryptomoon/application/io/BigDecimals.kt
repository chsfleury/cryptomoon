package fr.chsfleury.cryptomoon.application.io

import java.math.BigDecimal
import java.math.RoundingMode

object BigDecimals {

    fun BigDecimal.clean(): BigDecimal = setScale(2, RoundingMode.HALF_EVEN)
    fun BigDecimal.applyRate(r: BigDecimal?): BigDecimal = if (r != null) {
        this * r
    } else {
        this
    }

}