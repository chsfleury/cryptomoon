package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal
import java.time.Instant

class AccountSnapshot (
    val origin: String,
    val balances: Set<Balance>,
    val timestamp: Instant
) {
    companion object {
        const val ALL = "ALL"

        fun of(origin: String, timestamp: Instant, vararg balances: Collection<Balance>): AccountSnapshot = ofSeq(origin, timestamp, balances.asSequence())

        private fun ofSeq(origin: String, timestamp: Instant, sequence: Sequence<Collection<Balance>>): AccountSnapshot {
            val balanceMap = mutableMapOf<Currency, BigDecimal>()
            sequence
                .flatMap { it }
                .forEach {
                    balanceMap.compute(it.currency) { _, old ->
                        old?.plus(it.amount) ?: it.amount
                    }
                }
            val balanceSet = balanceMap.asSequence().sortedBy { it.key.symbol }.mapTo(LinkedHashSet()) { Balance(it.key, it.value) }
            return AccountSnapshot(origin, balanceSet, timestamp)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountSnapshot

        if (origin != other.origin) return false

        return true
    }

    override fun hashCode(): Int {
        return origin.hashCode()
    }
}