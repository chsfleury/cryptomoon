package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import java.math.BigDecimal

@JsonPropertyOrder("origin", "balances", "timestamp")
class AccountJson(
    val origin: String,
    val balances: Map<String, BigDecimal>,
    val timestamp: String
) {
    companion object {
        fun of(account: AccountSnapshot): AccountJson = AccountJson(
            account.origin,
            account.balances.asSequence().filterNot(Balance::isZero).map { it.currency.symbol to it.amount.stripTrailingZeros() }.sortedBy { it.first }.toMap(LinkedHashMap()),
            account.timestamp.toString()
        )
    }
}