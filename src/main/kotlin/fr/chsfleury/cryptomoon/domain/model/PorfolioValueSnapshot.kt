package fr.chsfleury.cryptomoon.domain.model

import java.math.BigDecimal
import java.time.Instant

class PorfolioValueSnapshot(
    val fiat: Fiat,
    val at: Instant,
    val amount: BigDecimal
)