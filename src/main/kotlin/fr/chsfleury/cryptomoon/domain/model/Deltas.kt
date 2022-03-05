package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.utils.FiatMap

class Deltas (
    val deltas: List<DeltaBalance>
) {
    val total: FiatMap by lazy {
        val map = FiatMap()
        deltas
            .asSequence()
            .filter { delta -> delta.value != null }
            .forEach { delta -> map += delta.value!! }
        map
    }
}