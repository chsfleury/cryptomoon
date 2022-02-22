package fr.chsfleury.cryptomoon.application.pebble

import com.mitchellbosecke.pebble.extension.AbstractExtension
import fr.chsfleury.cryptomoon.application.pebble.filter.BalanceFilter
import fr.chsfleury.cryptomoon.application.pebble.filter.FiatMapFilter
import fr.chsfleury.cryptomoon.application.pebble.filter.NotNullAddFilter

object CryptomoonExtension: AbstractExtension() {
    override fun getFilters() = mapOf(
        "fiatMap" to FiatMapFilter,
        "balance" to BalanceFilter,
        "notNullAdd" to NotNullAddFilter
    )
}