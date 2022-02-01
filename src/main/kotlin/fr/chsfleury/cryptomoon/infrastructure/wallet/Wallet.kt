package fr.chsfleury.cryptomoon.infrastructure.wallet

import fr.chsfleury.cryptomoon.domain.model.Currency
import java.math.BigDecimal

data class Wallet(
    val name: String,
    val balances: Map<Currency, BigDecimal>
)
