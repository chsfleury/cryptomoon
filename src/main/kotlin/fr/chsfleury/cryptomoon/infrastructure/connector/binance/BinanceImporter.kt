package fr.chsfleury.cryptomoon.infrastructure.connector.binance

import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoUnit

object BinanceImporter {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(' ')
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .toFormatter()

    fun extractBalancesFromStakingHistoryFile(inputStream: InputStream): Map<String, BigDecimal> {
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)
        val now = Instant.now()
        val balances = mutableMapOf<Currency, BigDecimal>()

        sheet.asSequence()
            .drop(1)
            .map { row ->
                val subscriptionDateValue = row.getCell(0).stringCellValue
                val subscriptionDate = LocalDateTime.parse(subscriptionDateValue, dateFormatter)

                val currency = Currencies[row.getCell(1).stringCellValue]
                val amount = BigDecimal(row.getCell(2).stringCellValue)
                val days = row.getCell(3).stringCellValue
                    .dropLastWhile { it != ' ' }
                    .trim()
                    .toLong()

                StakingHistory(currency, amount, subscriptionDate.toInstant(ZoneOffset.UTC), days)
            }
            .filterNot { it.isExpired(now) }
            .forEach { stakingEntry ->
                balances.merge(stakingEntry.currency, stakingEntry.amount) { a1, a2 -> a1 + a2 }
            }

        return balances.mapKeys { it.key.symbol }
    }

    class StakingHistory(
        val currency: Currency,
        val amount: BigDecimal,
        val startedAt: Instant,
        val days: Long
    ) {
        fun isExpired(now: Instant = Instant.now()): Boolean = startedAt.plus(days, ChronoUnit.DAYS) < now
    }
}