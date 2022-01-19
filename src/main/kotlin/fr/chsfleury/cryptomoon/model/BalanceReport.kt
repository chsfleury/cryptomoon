package fr.chsfleury.cryptomoon.model

import java.math.BigDecimal
import java.time.Instant

class BalanceReport(
    val balanceMap: Map<String, BigDecimal>,
    val balanceList: List<Balance>,
    val timestamp: Instant
) {
    companion object {
        fun of(vararg balances: List<Balance>): BalanceReport = ofSeq(balances.asSequence())

        private fun ofSeq(sequence: Sequence<List<Balance>>): BalanceReport {
            val balanceMap = mutableMapOf<String, BigDecimal>()
            sequence
                .flatMap { it }
                .forEach {
                    balanceMap.compute(it.currency) { _, old ->
                        old?.plus(it.amount) ?: it.amount
                    }
                }
            val balanceList = balanceMap.map { Balance(it.key, it.value) }
            return BalanceReport(balanceMap, balanceList, Instant.now())
        }

        fun merge(balanceReports: Collection<BalanceReport>): BalanceReport = ofSeq(balanceReports.asSequence().map(BalanceReport::balanceList))
    }
}