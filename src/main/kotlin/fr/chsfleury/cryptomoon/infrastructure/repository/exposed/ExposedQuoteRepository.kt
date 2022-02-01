package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository
import fr.chsfleury.cryptomoon.infrastructure.dto.BalanceRecord
import fr.chsfleury.cryptomoon.infrastructure.dto.QuoteRecord
import fr.chsfleury.cryptomoon.infrastructure.entities.QuoteEntity
import fr.chsfleury.cryptomoon.infrastructure.repository.execAndMap
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object ExposedQuoteRepository: QuoteRepository {

    private const val PLATFORM_QUOTES_QUERY = """
        SELECT sub.currency, sub.origin, sub.amount, sub.fiat, sub.rank, sub.at
        FROM (
             SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
             FROM quotes
             WHERE fiat = 'USD'
        ) sub
        WHERE rownumber = 1
        AND origin = '%s';
    """

    override fun getLatestQuotes(ticker: String): Quotes? {
        val quoteRecords: List<QuoteRecord> = transaction { PLATFORM_QUOTES_QUERY.format(ticker).execAndMap(::QuoteRecord) }
        val maxTimestamp: Instant? = quoteRecords.asSequence().map { it.timestamp }.maxOrNull()
        val quotes = quoteRecords.map { record ->
            Quote(record.currency, record.rank, record.fiat, record.price)
        }
        return maxTimestamp?.let { at -> Quotes(ticker, quotes, at) }
    }

    override fun insert(quotes: Quotes) {
        transaction {
            QuoteEntity.batchInsert(quotes.quoteList) {
                this[QuoteEntity.currency] = it.currency.symbol
                this[QuoteEntity.origin] = quotes.origin
                this[QuoteEntity.at] = quotes.timestamp
                this[QuoteEntity.fiat] = it.fiat.name
                this[QuoteEntity.price] = it.price
                this[QuoteEntity.rank] = it.rank
            }
        }
    }

}