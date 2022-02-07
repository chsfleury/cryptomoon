package fr.chsfleury.cryptomoon.application.pebble.filter

import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Fiat.EUR
import fr.chsfleury.cryptomoon.domain.model.Fiat.USD
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal
import java.text.DecimalFormat

object FiatMapFilter: Filter {
    private val round0 = DecimalFormat("#,###")
    private val round2 = DecimalFormat("#,###.00")
    private val round6 = DecimalFormat("#,##0.000000")
    override fun getArgumentNames() = listOf("key", "round")

    override fun apply(input: Any?, args: Map<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): String {
        if (input !is FiatMap) {
            error("input is not a FiatMap on line $lineNumber")
        }
        val keyString = args["key"] as? String ?: error("missing key param on line $lineNumber")
        val round = args["round"] as? Boolean ?: true
        val key = Fiat.valueOf(keyString)
        val decimal = input[key] ?: error("missing map entry with key $keyString on line $lineNumber")
        val fiat = when(key) {
            EUR -> "€"
            USD -> "$"
        }
        val value: String = when {
            round -> round0 .format(decimal)
            decimal < BigDecimal.ONE -> round6.format(decimal)
            else -> round2.format(decimal)
        }
        return "$fiat $value"
    }
}