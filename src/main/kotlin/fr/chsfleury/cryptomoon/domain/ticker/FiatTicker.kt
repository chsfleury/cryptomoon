package fr.chsfleury.cryptomoon.domain.ticker

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.utils.FiatMap

interface FiatTicker {

    fun tick(vararg fiats: Fiat): FiatMap

}