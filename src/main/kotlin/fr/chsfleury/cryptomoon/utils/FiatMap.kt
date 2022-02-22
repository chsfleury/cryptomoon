package fr.chsfleury.cryptomoon.utils

import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class FiatMap: EnumMap<Fiat, BigDecimal>(Fiat::class.java) {
    operator fun plusAssign(other: EnumMap<Fiat, BigDecimal>) {
        other.forEach { (fiat, value) ->
            merge(fiat, value, BigDecimal::plus)
        }
    }

    fun clean(): FiatMap {
        forEach { (fiat, value) ->
            this[fiat] = value.setScale(2, RoundingMode.HALF_EVEN)
        }
        return this
    }

    companion object {
        fun of(vararg pairs: Pair<Fiat, BigDecimal>): FiatMap {
            val map = FiatMap()
            map.putAll(pairs)
            return map
        }
    }
}