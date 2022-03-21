package fr.chsfleury.cryptomoon.application.pebble.filter

import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import java.math.BigDecimal

class ConversionRateFilter(
    private val currencyService: CurrencyService
): Filter {

    override fun getArgumentNames() = listOf("fiat")

    override fun apply(input: Any?, args: Map<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): Any {
        if (input == null) return ""

        val fiat = Fiat[args["fiat"] as? String ?: error("bad argument type 'fiat'")]
        val rate = currencyService.usdToFiat(fiat) ?: return ""

        return when(input) {
            is BigDecimal -> input * rate
            is Double -> input * rate.toDouble()
            is Float -> input * rate.toFloat()
            is Int -> input * rate.toDouble()
            is Long -> input * rate.toDouble()
            else -> ""
        }
    }
}