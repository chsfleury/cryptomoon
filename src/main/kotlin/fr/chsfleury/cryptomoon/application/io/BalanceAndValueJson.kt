package fr.chsfleury.cryptomoon.application.io

import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.domain.model.DeltaBalance
import java.math.BigDecimal

class BalanceAndValueJson(
    val currency: String,
    val balance: BigDecimal,
    val value: BigDecimal
) {
    companion object {
        fun of(balance: DeltaBalance, conversionRate: BigDecimal?) = BalanceAndValueJson(balance.currency.symbol, balance.amount, balance.valueUSD.applyRate(conversionRate).clean())
    }
}