package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.AccountJson
import fr.chsfleury.cryptomoon.application.io.DeltaJson
import fr.chsfleury.cryptomoon.application.io.PortfolioJson
import fr.chsfleury.cryptomoon.domain.service.*
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers.COINMARKETCAP
import io.javalin.http.Context

class PortfolioController(
    private val portfolioService: PortfolioService,
    private val quoteService: QuoteService,
    private val athService: ATHService,
    private val balanceService: BalanceService
) {

    fun getPortfolio(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val mergedParam = ctx.queryParam("merged")
        val merged = mergedParam != null && (mergedParam.isEmpty() || mergedParam == "true")
        val portfolio = portfolioService.getPortfolio(portfolioName, merged)
        val portfolioStats = portfolio.stats(quoteService, athService, COINMARKETCAP)
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
        val accountStats = account.stats(quoteService, athService, COINMARKETCAP)
        ctx.json(AccountJson.of(accountStats))
    }

    fun getPortfolioDelta(ctx: Context) {
        val portfolioName = ctx.pathParam("portfolio")
        val days = ctx.queryParam("days")?.toIntOrNull() ?: 7
        val accountNames = portfolioService.getPorfolioAccountNames(portfolioName)
        val deltaJson = DeltaJson.of(balanceService.getDelta(accountNames, days))
        ctx.json(deltaJson)
    }
}