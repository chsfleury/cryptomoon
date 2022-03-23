package fr.chsfleury.cryptomoon.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.HALF_EVEN
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.ceil
import kotlin.math.log10

object BigDecimals {
    private val EPSILON: BigDecimal = BigDecimal.valueOf(0.000001)

    private val THOUSAND = BigDecimal(1000)
    private val MILLION = BigDecimal(1_000_000.0)
    private val BILLION = BigDecimal(1_000_000_000.0)
    private val FORMAT_LOCALE = DecimalFormatSymbols(Locale.ENGLISH)
    private val LOG_10_1_FORMAT = DecimalFormat("0.####", FORMAT_LOCALE)
    private val LOG_10_2_FORMAT = DecimalFormat("0.###", FORMAT_LOCALE)
    private val LOG_10_3_FORMAT = DecimalFormat("0.##", FORMAT_LOCALE)
    private val LOG_10_4_FORMAT = DecimalFormat("#,###.#", FORMAT_LOCALE)
    private val LOG_10_5_FORMAT = DecimalFormat("#,###", FORMAT_LOCALE)

    infix fun BigDecimal.eq(other: BigDecimal): Boolean {
        return minus(other).abs() <= EPSILON
    }

    fun BigDecimal.pretty(): String {
        val doubleValue = toDouble()
        val log10 = ceil(log10(doubleValue)).toInt()
        return when {
            log10 <= -4 -> "<0.0001"
            log10 <= 1 -> LOG_10_1_FORMAT.format(this)
            log10 == 2 -> LOG_10_2_FORMAT.format(this)
            log10 == 3 -> LOG_10_3_FORMAT.format(this)
            log10 == 4 -> LOG_10_4_FORMAT.format(this)
            log10 == 5 -> LOG_10_5_FORMAT.format(this)
            log10 == 6 -> LOG_10_4_FORMAT.format(divide(THOUSAND, HALF_EVEN)) + 'K'
            log10 == 7 -> LOG_10_5_FORMAT.format(divide(THOUSAND, HALF_EVEN)) + 'K'
            log10 == 8 -> LOG_10_3_FORMAT.format(divide(MILLION, HALF_EVEN)) + 'M'
            log10 == 9 -> LOG_10_4_FORMAT.format(divide(MILLION, HALF_EVEN)) + 'M'
            log10 == 10 -> LOG_10_5_FORMAT.format(divide(MILLION, HALF_EVEN)) + 'M'
            log10 == 11 -> LOG_10_3_FORMAT.format(divide(BILLION, HALF_EVEN)) + 'B'
            log10 == 12 -> LOG_10_4_FORMAT.format(divide(BILLION, HALF_EVEN)) + 'B'
            else -> LOG_10_5_FORMAT.format(divide(BILLION, HALF_EVEN)) + 'B'
        }
    }

}