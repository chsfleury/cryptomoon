package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.model.impl.SimpleQuote
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository
import fr.chsfleury.cryptomoon.infrastructure.dto.QuoteRecord
import fr.chsfleury.cryptomoon.infrastructure.entities.QuoteEntity
import fr.chsfleury.cryptomoon.infrastructure.repository.execAndMap
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object ExposedQuoteRepository: QuoteRepository {

    private const val QUOTES_QUERY = """
        SELECT sub.currency, sub.amount, sub.rank, sub.at, sub.ath
        FROM (
             SELECT *, ROW_NUMBER() over (PARTITION BY currency ORDER BY at DESC ) rownumber
             FROM quotes
        ) sub
        WHERE rownumber = 1;
    """

    override fun getLatestQuotes(): List<Quote> {
        val quoteRecords: List<QuoteRecord> = transaction { QUOTES_QUERY.execAndMap(::QuoteRecord) }
        val quotes = quoteRecords.map { record ->
            SimpleQuote(record.currency, record.rank, record.price)
        }
        return quotes
    }

    override fun save(quotes: List<Quote>) {
        val now = Instant.now()
        transaction {
            QuoteEntity.batchInsert(quotes) {
                this[QuoteEntity.currency] = it.currency.symbol
                this[QuoteEntity.at] = now
                this[QuoteEntity.price] = it.priceUSD
                this[QuoteEntity.rank] = it.rank
            }
        }
    }

}