package fr.chsfleury.cryptomoon.model

import java.math.BigDecimal
import java.time.Instant

class BalanceReport(
    val balances: Map<String, BigDecimal>,
    val timestamp: Instant
) {
    companion object {
        fun of(vararg balances: List<Balance>): BalanceReport {
            val balanceMap = mutableMapOf<String, BigDecimal>()
            balances.asSequence()
                .flatMap { it }
                .forEach {
                    balanceMap.compute(it.currency) { _, old ->
                        old?.plus(it.amount) ?: it.amount
                    }
                }
            return BalanceReport(balanceMap, Instant.now())
        }
    }

    override fun toString(): String {
        return "BalanceReport(balances=$balances, timestamp=$timestamp)"
    }
}