package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal
import java.sql.Timestamp

class FiatPair (
    val fiat: Fiat,
    val amount: BigDecimal,
    val at: Timestamp
)