package fr.chsfleury.cryptomoon.model

import java.math.BigDecimal

data class Balance (
    val currency: String,
    val amount: BigDecimal
)