package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.domain.model.DeltaBalance
import fr.chsfleury.cryptomoon.domain.model.Sum
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

@JsonPropertyOrder("total", "delta")
data class DeltaJson (
    val delta: List<BalanceAndValueJson>,
    val total: BigDecimal
) {
    companion object {
        fun of(delta: List<DeltaBalance>, conversionRate: BigDecimal?): DeltaJson {
            val total = Sum()
            val deltaItems = delta.map {
                total += it.valueUSD
                BalanceAndValueJson.of(it, conversionRate)
            }
            return DeltaJson(
                deltaItems,
                total.value().applyRate(conversionRate).clean()
            )
        }
    }
}