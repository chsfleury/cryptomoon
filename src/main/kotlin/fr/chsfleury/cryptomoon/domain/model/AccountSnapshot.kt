package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.Balance.Companion.toBalance
import java.math.BigDecimal
import java.time.Instant

class AccountSnapshot (
    val origin: String,
    val balances: Set<Balance>,
    val timestamp: Instant
) {
    companion object {
        const val ALL = "ALL"

        fun empty() = AccountSnapshot("", emptySet(), Instant.now())

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
            val balanceSet = balanceMap.asSequence().sortedBy { it.key.symbol }.mapTo(LinkedHashSet()) { it.toBalance() }
            return AccountSnapshot(origin, balanceSet, timestamp)
        }

        fun merge(accountSnapshots: Collection<AccountSnapshot>, origin: String? = null): AccountSnapshot? {
            if (accountSnapshots.isEmpty()) {
                return null
            }
            val finalOrigin = origin ?: accountSnapshots.asSequence()
                .map(AccountSnapshot::origin)
                .sorted()
                .joinToString("~")

            val timestamp = accountSnapshots.minOf(AccountSnapshot::timestamp)
            return of(finalOrigin, timestamp, accountSnapshots.flatMap(AccountSnapshot::balances))
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