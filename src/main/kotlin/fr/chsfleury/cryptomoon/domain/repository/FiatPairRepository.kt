package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal
import java.time.Instant

interface FiatPairRepository {
    fun all(): FiatMap
    fun getUsdToFiat(fiat: Fiat): BigDecimal?

    fun save(rates: FiatMap)
}