package fr.chsfleury.cryptomoon.application

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.mitchellbosecke.pebble.loader.FileLoader
import com.mysql.cj.jdbc.Driver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import fr.chsfleury.cryptomoon.application.controller.*
import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.HighchartsFormatter
import fr.chsfleury.cryptomoon.application.page.DashboardPage
import fr.chsfleury.cryptomoon.application.pebble.CryptomoonExtension
import fr.chsfleury.cryptomoon.domain.Execution
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.service.*
import fr.chsfleury.cryptomoon.domain.ticker.ATHTicker
import fr.chsfleury.cryptomoon.domain.trigger.ATHTrigger
import fr.chsfleury.cryptomoon.domain.trigger.BalanceTrigger
import fr.chsfleury.cryptomoon.domain.trigger.PortfolioValueTrigger
import fr.chsfleury.cryptomoon.domain.trigger.QuoteTrigger
import fr.chsfleury.cryptomoon.infrastructure.configuration.LocalFileConfiguration
import fr.chsfleury.cryptomoon.infrastructure.entities.*
import fr.chsfleury.cryptomoon.infrastructure.repository.exposed.*
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFileAccountRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFileConnectorRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFilePortfolioRepository
import fr.chsfleury.cryptomoon.infrastructure.repository.file.LocalFileTickerRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers
import fr.chsfleury.cryptomoon.infrastructure.wallet.LocalWalletsFile
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import io.javalin.http.staticfiles.Location
import io.javalin.plugin.rendering.JavalinRenderer
import io.javalin.plugin.rendering.template.JavalinPebble
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration
import javax.sql.DataSource

object Cryptomoon : Logging {
    private val log = logger()

    @JvmStatic
    fun main(args: Array<String>) {
        val logLevel: String? = LocalFileConfiguration["log"]
        if (logLevel != null) {
            val rootLogger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
            rootLogger.level = Level.valueOf(logLevel)
            log.info("Set root log level to {}", logLevel)
        }

        val executionMode: String? = LocalFileConfiguration["mode"]
        Execution.set(executionMode)

        val port = LocalFileConfiguration["server", "port"] ?: 7777

        val dataSource = createDatasource()
        Database.connect(dataSource)

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(BalanceEntity, TriggerEntity, QuoteEntity, ATHEntity, PortfolioHistoryEntity)
        }

        // SERVICES
        val tickerService = TickerService(LocalFileTickerRepository)
        val quoteService = QuoteService(ExposedQuoteRepository, ExposedFiatPairRepository)
        val connectorService = ConnectorService(LocalFileConnectorRepository)
        val accountService = AccountService(listOf(ExposedAccountRepository, LocalFileAccountRepository))
        val athService = ATHService(ExposedATHRepository)
        val portfolioService = PortfolioService(LocalFilePortfolioRepository, ExposedPortfolioHistoryRepository, connectorService, accountService)

        // CONTROLLERS
        val portfolioController = PortfolioController(portfolioService, quoteService, athService)
        val tickerController = TickerController(tickerService)
        val fiatController = FiatController(quoteService)
        val chartController = ChartController(portfolioService, quoteService, athService, listOf(HighchartsFormatter))
        val athController = AthController(tickerService[Tickers.LIVECOINWATCH] as ATHTicker)

        val javalin = Javalin.create(this::configureJavalin)

        if (Execution.isStandard()) {
            // TRIGGERS
            val balanceTrigger = BalanceTrigger(connectorService, accountService)
            val portfolioValueTrigger = PortfolioValueTrigger(portfolioService, quoteService, athService, PortfolioValueType.CURRENT, "portfolioValue", Duration.ofHours(1))
            val athPortfolioValueTrigger = PortfolioValueTrigger(portfolioService, quoteService, athService, PortfolioValueType.ATH, "portfolioValueATH", Duration.ofDays(1))
            val usdQuoteTrigger = QuoteTrigger(tickerService, quoteService, accountService, Fiat.USD, "usdQuote", after = listOf(portfolioValueTrigger))
            val eurQuoteTrigger = QuoteTrigger(tickerService, quoteService, accountService, Fiat.EUR, "eurQuote", Duration.ofDays(1), listOf(usdQuoteTrigger))
            val athTrigger = ATHTrigger(
                tickerService[Tickers.LIVECOINWATCH] as ATHTicker,
                athService,
                accountService
            )

            val triggerService = TriggerService(
                ExposedTriggerRepository,
                balanceTrigger, eurQuoteTrigger, usdQuoteTrigger, athTrigger, portfolioValueTrigger, athPortfolioValueTrigger
            ).start()

            // CONTROLLERS
            val triggerController = TriggerController(triggerService)

            javalin
                .get("/api/v1/triggers/_check", triggerController::checkTriggers)
                .get("/api/v1/triggers/_force", triggerController::forceTriggers)
        }

        // PAGES
        configureRenderer()
        val dashboardPage = DashboardPage(portfolioService, quoteService, athService)

        javalin
            .get("/", dashboardPage::getDashboard)
            .get("/api/v1/portfolios", portfolioController::getPortfolioNames)
            .get("/api/v1/portfolios/{portfolio}", portfolioController::getPortfolio)
            .get("/api/v1/portfolios/{portfolio}/assets-distribution", chartController::getAssetDistribution)
            .get("/api/v1/portfolios/{portfolio}/accounts", portfolioController::getPortfolioAccountNames)
            .get("/api/v1/portfolios/{portfolio}/accounts/{origin}", portfolioController::getPortfolioAccount)
            .get("/api/v1/portfolios/{portfolio}/history") { ctx -> chartController.getPortfolioHistory(PortfolioValueType.CURRENT, ctx) }
            .get("/api/v1/portfolios/{portfolio}/history/{valueType}", chartController::getPortfolioHistory)
            .get("/api/v1/tickers", tickerController::getTickerNames)
            .get("/api/v1/tickers/{ticker}", tickerController::getTick)
            .get("/api/v1/fiats", fiatController::getFiatPair)
            .get("/api/v1/fiats/last", fiatController::getLastFiatPair)
            .get("/api/v1/aths/{symbol}", athController::getAth)

        javalin.start(port)
        // LISTENERS
        accountService.addListener(portfolioService)
        quoteService.addListener(accountService)

        Runtime.getRuntime().addShutdownHook(Thread { LocalWalletsFile.stopWatch() })
    }

    private fun createDatasource(): DataSource {
        val datasourceConfiguration: Map<String, String> = LocalFileConfiguration["datasource"] ?: error("missing datasource configuration")
        val user = datasourceConfiguration["username"] ?: error("missing datasource username")
        val url = datasourceConfiguration["url"] ?: error("missing datasource url")
        val config = HikariConfig().apply {
            jdbcUrl = url
            username = user
            password = datasourceConfiguration["password"] ?: error("missing datasource password")
            driverClassName = when (datasourceConfiguration["type"] ?: error("missing datasource type")) {
                "MYSQL" -> Driver::class.java.name
                else -> error("unsupported datasource")
            }
        }
        log.info("Datasource: user={}, url={}", user, url)
        return HikariDataSource(config)
    }

    private fun configureJavalin(config: JavalinConfig) {
        config.showJavalinBanner = false

        val assetPath = LocalFileConfiguration["assetsPath"] ?: "default"
        if (assetPath == "default") {
            log.info("load assets from classpath")
            config.addStaticFiles { staticFiles ->
                staticFiles.hostedPath = "/assets"
                staticFiles.directory = "/assets"
                staticFiles.location = Location.CLASSPATH
                staticFiles.precompress = false
            }
        } else {
            log.info("load assets from {}", assetPath)
            config.addStaticFiles { staticFiles ->
                staticFiles.hostedPath = "/assets"
                staticFiles.directory = assetPath
                staticFiles.location = Location.EXTERNAL
                staticFiles.precompress = false
            }
        }
    }

    private fun configureRenderer() {
        val templatesPath: String = LocalFileConfiguration["templatesPath"] ?: "default"
        val loader = if (templatesPath == "default") {
            log.info("load templates from classpath")
            ClasspathLoader().apply {
                prefix = "./templates"
            }
        } else {
            log.info("load assets from {}", templatesPath)
            FileLoader().apply {
                prefix = templatesPath
            }
        }

        val templateCached = "true" != System.getProperty("dev")
        if (templateCached) {
            log.info("templates will be cached")
        } else {
            log.info("templates will not be cached")
        }
        val engine = PebbleEngine.Builder()
            .cacheActive(templateCached)
            .loader(loader)
            .strictVariables(false)
            .extension(CryptomoonExtension)
            .build()

        JavalinPebble.configure(engine)
        JavalinRenderer.register(JavalinPebble, ".html")
    }
}