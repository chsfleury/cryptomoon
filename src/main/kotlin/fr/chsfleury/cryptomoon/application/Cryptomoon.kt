package fr.chsfleury.cryptomoon.application

import com.mysql.cj.jdbc.Driver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import fr.chsfleury.cryptomoon.application.controller.PortfolioController
import fr.chsfleury.cryptomoon.application.controller.TickerController
import fr.chsfleury.cryptomoon.application.io.QuotesJson
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.service.*
import fr.chsfleury.cryptomoon.domain.ticker.Ticker
import fr.chsfleury.cryptomoon.domain.trigger.BalanceTrigger
import fr.chsfleury.cryptomoon.domain.trigger.QuoteTrigger
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration
import fr.chsfleury.cryptomoon.infrastructure.entities.BalanceEntity
import fr.chsfleury.cryptomoon.infrastructure.entities.QuoteEntity
import fr.chsfleury.cryptomoon.infrastructure.entities.TriggerEntity
import fr.chsfleury.cryptomoon.infrastructure.http.Http
import fr.chsfleury.cryptomoon.infrastructure.repository.exposed.ExposedAccountRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.exposed.ExposedQuoteRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.exposed.ExposedTriggerRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFileConnectorRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFilePortfolioRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFileTickerRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.http.HttpClient
import java.time.Duration
import javax.sql.DataSource

object Cryptomoon: Logging {
    private val log = logger()

    @JvmStatic
    fun main(args: Array<String>) {

        val port = LocalFileConfiguration["server", "port"] ?: 7777

        val dataSource = createDatasource()
        Database.connect(dataSource)

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(BalanceEntity, TriggerEntity, QuoteEntity)
        }

        // SERVICES
        val tickerService = TickerService(LocalFileTickerRepository)
        val quoteService = QuoteService(ExposedQuoteRepository)
        val connectorService = ConnectorService(LocalFileConnectorRepository)
        val accountService = AccountService(ExposedAccountRepository)
        val portfolioService = PortfolioService(LocalFilePortfolioRepository, connectorService, accountService)

        // CONTROLLERS
        val portfolioController = PortfolioController(portfolioService)
        val tickerController = TickerController(tickerService)

        Javalin.create().start(port)
            .get("/portfolios", portfolioController::getPortfolioNames)
            .get("/portfolios/{portfolio}", portfolioController::getPortfolio)
            .get("/portfolios/{portfolio}/accounts", portfolioController::getPortfolioAccountNames)
            .get("/portfolios/{portfolio}/accounts/{origin}", portfolioController::getPortfolioAccount)
            .get("/tickers", tickerController::getTickerNames)
            .get("/tickers/{ticker}", tickerController::getTick)

        // TRIGGERS
        val balanceTrigger = BalanceTrigger(connectorService, accountService)
        val usdQuoteTrigger = QuoteTrigger(tickerService, quoteService, Fiat.USD, "usdQuote")
        val eurQuoteTrigger = QuoteTrigger(tickerService, quoteService, Fiat.EUR, "eurQuote", Duration.ofDays(1), listOf(usdQuoteTrigger))
        TriggerService(
            ExposedTriggerRepository,
            balanceTrigger, usdQuoteTrigger, eurQuoteTrigger
        ).start()
    }

    private fun createDatasource(): DataSource {
        val datasourceConfiguration: Map<String, String> = LocalFileConfiguration["datasource"] ?: error("missing datasource configuration")
        val config = HikariConfig().apply {
            jdbcUrl = datasourceConfiguration["url"] ?: error("missing datasource url")
            username = datasourceConfiguration["username"] ?: error("missing datasource username")
            password = datasourceConfiguration["password"] ?: error("missing datasource password")
            driverClassName = when(datasourceConfiguration["type"] ?: error("missing datasource type")) {
                "MYSQL" -> Driver::class.java.name
                else -> error("unsupported datasource")
            }
        }
        return HikariDataSource(config)
    }

}