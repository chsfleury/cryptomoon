package fr.chsfleury.cryptomoon.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal
import java.time.Instant

@JsonPropertyOrder("balances", "timestamp")
class BalanceReportJson(
    balanceMap: Map<String, BigDecimal>,
     val timestamp: Instant
) {
    val balances = balanceMap.toSortedMap()
}