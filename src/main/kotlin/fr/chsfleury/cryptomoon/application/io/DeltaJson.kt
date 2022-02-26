package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.DeltaBalance
import fr.chsfleury.cryptomoon.utils.FiatMap

@JsonPropertyOrder("total", "delta")
data class DeltaJson (
    val delta: List<BalanceAndValueJson>,
    val total: FiatMap
) {
    companion object {
        fun of(delta: List<DeltaBalance>): DeltaJson {
            val total = FiatMap()
            val deltaItems = delta.map {
                if (it.value != null) {
                    total += it.value
                }
                BalanceAndValueJson.of(it)
            }
            return DeltaJson(
                deltaItems,
                total.clean()
            )
        }
    }
}