package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.QuotesJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.TickerService
import io.javalin.http.Context

class TickerController(
    private val tickerService: TickerService
) {

    fun getTickerNames(ctx: Context) {
        ctx.json(tickerService.names().toSortedSet())
    }

    fun getTick(ctx: Context) {
        val tickerName = ctx.pathParam("ticker")
        val ticker = tickerService[tickerName] ?: error("unknown ticker")
        val fiat = Fiat.valueOf(ctx.queryParam("fiat") ?: "USD")
        val quotes = ticker.tickAll(fiat)
        ctx.json(QuotesJson.of(quotes))
    }

}