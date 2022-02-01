package fr.chsfleury.cryptomoon.domain.repository

import java.math.BigDecimal
import java.time.Instant

interface FiatPairRepository {
    fun getUsdToEuros(lastDays: Int = 30): List<Pair<Instant, BigDecimal>>
}