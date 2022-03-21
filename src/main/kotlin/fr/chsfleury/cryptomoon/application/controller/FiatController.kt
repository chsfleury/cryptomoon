package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.controller.utils.PathParams.fiatPathParam
import fr.chsfleury.cryptomoon.application.controller.utils.QueryParams.fiatQueryParam
import fr.chsfleury.cryptomoon.application.io.FiatPairJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.CurrencyService
import io.javalin.http.Context
import java.math.BigDecimal

class FiatController(
    private val currencyService: CurrencyService
) {
    fun getAllFiatPairs(ctx: Context) {
        val fiatPairs = currencyService.rates().map { (fiat: Fiat, rate: BigDecimal) ->
            FiatPairJson(fiat, rate)
        }
        ctx.json(fiatPairs)
    }

    fun getFiatPair(ctx: Context) {
        val to = ctx.fiatPathParam()
        val rate = currencyService
            .usdToFiat(ctx.fiatQueryParam())

        if (rate == null) {
            ctx.status(404)
        } else {
            ctx.json(FiatPairJson(to, rate))
        }
    }

}