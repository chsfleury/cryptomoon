package fr.chsfleury.cryptomoon.application.pebble.filter

import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import fr.chsfleury.cryptomoon.utils.BigDecimals.pretty
import java.math.BigDecimal
import java.text.DecimalFormat

object PrettyDecimalFilter: Filter {
    override fun getArgumentNames() = emptyList<String>()

    override fun apply(input: Any?, args: Map<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): String {
        if (input !is BigDecimal) {
            error("input is not a BigDecimal on line $lineNumber")
        }
        return input.pretty()
    }
}