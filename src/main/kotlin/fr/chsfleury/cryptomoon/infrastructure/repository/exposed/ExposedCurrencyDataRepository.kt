package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.repository.CurrencyDataRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.CurrencyEntity
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object ExposedCurrencyDataRepository: CurrencyDataRepository {

    override fun all(): Set<CurrencyData> {
        return transaction {
            CurrencyEntity.selectAll().mapTo(mutableSetOf()) {
                CurrencyData(
                    Currencies[it[CurrencyEntity.currency]],
                    it[CurrencyEntity.price],
                    it[CurrencyEntity.ath],
                    it[CurrencyEntity.rank]
                )
            }
        }
    }

    override fun save(currencyData: CurrencyData) {
        transaction {
            if(exists(currencyData.currency)) {
                CurrencyEntity.update({ CurrencyEntity.currency eq currencyData.currency.symbol }) {
                    it[price] = currencyData.priceUSD
                    it[ath] = currencyData.athUSD
                    it[rank] = currencyData.rank
                }
            } else {
                CurrencyEntity.insert {
                    it[currency] = currencyData.currency.symbol
                    it[price] = currencyData.priceUSD
                    it[ath] = currencyData.athUSD
                    it[rank] = currencyData.rank
                }
            }
        }
    }

    override fun saveATH(allTimeHigh: ATH) {
        transaction {
            doSaveATH(allTimeHigh)
        }
    }

    override fun saveATH(aths: Collection<ATH>) {
        transaction {
            aths.forEach {
                doSaveATH(it)
            }
        }
    }

    private fun doSaveATH(allTimeHigh: ATH) {
        if(exists(allTimeHigh.currency)) {
            CurrencyEntity.update({ CurrencyEntity.currency eq allTimeHigh.currency.symbol }) {
                it[ath] = allTimeHigh.athUSD
            }
        } else {
            CurrencyEntity.insert {
                it[currency] = allTimeHigh.currency.symbol
                it[ath] = allTimeHigh.athUSD
            }
        }
    }

    override fun saveQuote(quote: Quote) {
        transaction {
            doSaveQuote(quote)
        }
    }

    private fun doSaveQuote(quote: Quote) {
        if (exists(quote.currency)) {
            CurrencyEntity.update({ CurrencyEntity.currency eq quote.currency.symbol }) {
                it[price] = quote.priceUSD
                it[rank] = quote.rank
            }
        } else {
            CurrencyEntity.insert {
                it[currency] = quote.currency.symbol
                it[price] = quote.priceUSD
                it[rank] = quote.rank
            }
        }
    }

    override fun saveQuote(quotes: Collection<Quote>) {
        transaction {
            quotes.forEach {
                doSaveQuote(it)
            }
        }
    }

    private fun exists(currency: Currency): Boolean {
        return CurrencyEntity.select { CurrencyEntity.currency eq currency.symbol }.toList().isNotEmpty()
    }

}