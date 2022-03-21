package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class MarketData(
    val currencies: List<CurrencyData>,
    val rates: FiatMap = FiatMap()
) {
    private val currencyMap by lazy { currencies.associateBy { it.currency } }

    operator fun get(currency: Currency): CurrencyData? = currencyMap[currency]
    operator fun get(fiat: Fiat): BigDecimal? = rates[fiat]
}