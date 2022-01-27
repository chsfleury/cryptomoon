package fr.chsfleury.cryptomoon.infrastructure.dto

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import java.time.Instant

class MutableAccount(
    val origin: String,
    val balances: MutableList<Balance>,
    var timestamp: Instant
) {
    fun toAccount() = AccountSnapshot.of(origin, timestamp, balances)
}