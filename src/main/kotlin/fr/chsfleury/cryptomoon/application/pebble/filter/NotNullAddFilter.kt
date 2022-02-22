package fr.chsfleury.cryptomoon.application.pebble.filter

import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate

object NotNullAddFilter: Filter {
    override fun getArgumentNames() = listOf("content")

    override fun apply(input: Any?, args: Map<String, Any>, self: PebbleTemplate, context: EvaluationContext, lineNumber: Int): String {
        return if (input == null) {
            ""
        } else {
            input.toString() + (args["content"] ?: "")
        }
    }

}