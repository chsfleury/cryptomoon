package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class DeltaBalance(
    currency: Currency,
    amount: BigDecimal,
    val value: FiatMap?
): Balance(currency, amount)