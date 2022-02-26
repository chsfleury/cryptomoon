package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.domain.model.DeltaBalance
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class BalanceAndValueJson(
    val currency: String,
    val balance: BigDecimal,
    val value: FiatMap?
) {
    companion object {
        fun of(balance: DeltaBalance) = BalanceAndValueJson(balance.currency.symbol, balance.amount, balance.value?.clean())
    }
}