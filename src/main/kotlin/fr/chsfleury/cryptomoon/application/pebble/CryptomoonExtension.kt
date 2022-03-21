package fr.chsfleury.cryptomoon.application.pebble

import com.mitchellbosecke.pebble.extension.AbstractExtension
import fr.chsfleury.cryptomoon.application.pebble.filter.BalanceFilter
import fr.chsfleury.cryptomoon.application.pebble.filter.ConversionRateFilter
import fr.chsfleury.cryptomoon.application.pebble.filter.FiatMapFilter
import fr.chsfleury.cryptomoon.application.pebble.filter.NotNullAddFilter
import fr.chsfleury.cryptomoon.domain.service.CurrencyService

class CryptomoonExtension(
    private val currencyService: CurrencyService
): AbstractExtension() {
    override fun getFilters() = mapOf(
        "rate" to ConversionRateFilter(currencyService),
        "fiatMap" to FiatMapFilter,
        "balance" to BalanceFilter,
        "notNullAdd" to NotNullAddFilter
    )
}