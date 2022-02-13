package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.AccountJson
import fr.chsfleury.cryptomoon.application.io.PortfolioHistoryJson
import fr.chsfleury.cryptomoon.application.io.PortfolioJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.service.AccountService
import fr.chsfleury.cryptomoon.domain.service.PortfolioService
import fr.chsfleury.cryptomoon.domain.service.QuoteService
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers.COINMARKETCAP
import io.javalin.http.Context

class PortfolioController(
    private val portfolioService: PortfolioService,
    private val accountService: AccountService,
    private val quoteService: QuoteService
) {

    fun getPortfolio(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val mergedParam = ctx.queryParam("merged")
        val merged = mergedParam != null && (mergedParam.isEmpty() || mergedParam == "true")
        val portfolio = portfolioService.getPortfolio(portfolioName, merged)
        val portfolioStats = portfolioService.computeStats(portfolio, COINMARKETCAP)
        ctx.json(PortfolioJson.of(portfolioStats))
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
        val accountStats = accountService.computeStats(account, COINMARKETCAP)
        ctx.json(AccountJson.of(accountStats))
    }

    fun getPortfolioHistory(ctx: Context) {
        val portfolioValueType = PortfolioValueType.valueOf(ctx.pathParam("valueType").uppercase())
        getPortfolioHistory(portfolioValueType, ctx)
    }

    fun getPortfolioHistory(portfolioValueType: PortfolioValueType, ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val days = ctx.queryParam("days")?.toIntOrNull() ?: 7
        val fiat = ctx.queryParam("fiat")?.let { Fiat.valueOf(it.uppercase()) } ?: Fiat.USD
        val history = portfolioService.getHistory(portfolioName, portfolioValueType, fiat, days)
        ctx.json(PortfolioHistoryJson.of(history))
    }
}