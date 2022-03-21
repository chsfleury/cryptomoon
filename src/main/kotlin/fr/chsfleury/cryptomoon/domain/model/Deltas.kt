package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal

class Deltas (
    val deltas: List<DeltaBalance>
) {
    val total: BigDecimal by lazy {
        deltas.sumOf { it.valueUSD }
    }

    override fun toString(): String {
        return "Deltas(total=$total, deltas=$deltas)"
    }


}