package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.application.io.AccountJson
import fr.chsfleury.cryptomoon.application.io.PortfolioJson
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
        val portfolio = portfolioService.getPorfolio(portfolioName, merged)
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
        val account = portfolioService.getPorfolioAccount(portfolioName, origin) ?: error("unknown origin")
        val accountStats = accountService.computeStats(account, COINMARKETCAP)
        ctx.json(AccountJson.of(accountStats))
    }

}