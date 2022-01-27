package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.Quotes
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.QuoteEntity
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.transactions.transaction

object ExposedQuoteRepository: QuoteRepository {

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