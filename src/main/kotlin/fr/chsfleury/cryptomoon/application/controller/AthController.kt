package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.AthJson
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.ticker.ATHTicker
import io.javalin.http.Context

class AthController(
    private val athTicker: ATHTicker
) {

    fun getAth(ctx: Context) {
        val currency = ctx.pathParam("symbol").let { Currencies[it] }
        val ath = athTicker.getATH(currency)

        if (ath == null) ctx.status(404)
        else ctx.json(AthJson.of(ath))
    }

}