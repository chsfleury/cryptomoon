package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.controller.utils.QueryParams.fiatQueryParam
import fr.chsfleury.cryptomoon.application.io.AccountJson
import fr.chsfleury.cryptomoon.application.io.DeltaJson
import fr.chsfleury.cryptomoon.application.io.PortfolioJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.*
import io.javalin.http.Context
import java.math.BigDecimal

class PortfolioController(
    private val portfolioService: PortfolioService,
    private val currencyService: CurrencyService,
    private val balanceService: BalanceService
) {

    fun getPortfolio(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val mergedParam = ctx.queryParam("merged")
        val merged = mergedParam != null && (mergedParam.isEmpty() || mergedParam == "true")
        val portfolio = portfolioService.getPortfolio(portfolioName, merged)
        val portfolioStats = portfolio.stats(currencyService.marketData())

        ctx.json(PortfolioJson.of(portfolioStats, getConversionRate(ctx)))
    }

    fun getPortfolioNames(ctx: Context) {
        ctx.json(portfolioService.getPortfolioNames().toSortedSet())
    }

    fun getPortfolioAccountNames(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        ctx.json(portfolioService.getPorfolioAccountNames(portfolioName).toSortedSet())
    }

    fun getPortfolioAccount(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val origin = ctx.pathParam("origin")
        val account = portfolioService.getPortfolioAccount(portfolioName, origin) ?: error("unknown origin")
        val quotes = currencyService.marketData()
        val accountStats = account.stats(quotes)
        ctx.json(AccountJson.of(accountStats, getConversionRate(ctx)))
    }

    fun getPortfolioDelta(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val days = ctx.queryParam("days")?.toIntOrNull() ?: 7
        val accountNames = portfolioService.getPorfolioAccountNames(portfolioName)
        val deltaJson = DeltaJson.of(balanceService.getDelta(accountNames, days), getConversionRate(ctx))
        ctx.json(deltaJson)
    }

    private fun getConversionRate(ctx: Context): BigDecimal? = ctx.fiatQueryParam().takeIf { it != Fiat.USD }?.let { currencyService.usdToFiat(it) }
}