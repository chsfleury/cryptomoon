package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.FiatPairJson
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import io.javalin.http.Context

class FiatController(
    private val quoteService: QuoteService
) {

    fun getFiatPair(ctx: Context) {
        val body = quoteService
            .usdToEur(ctx.queryParam("days")?.toIntOrNull() ?: 30)
            .map(FiatPairJson::of)

        ctx.json(body)
    }

    fun getLastFiatPair(ctx: Context) {
        ctx.json(quoteService.usdToEur(1)[0].let(FiatPairJson::of))
    }

}