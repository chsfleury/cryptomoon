package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.DeltaBalance
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.Quotes
import fr.chsfleury.cryptomoon.domain.repository.BalanceRepository
import fr.chsfleury.cryptomoon.infrastructure.ticker.Tickers.COINMARKETCAP
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class BalanceService(
    private val quoteService: QuoteService,
    private val balanceRepository: BalanceRepository
) {
    fun getBalance(origins: Collection<String>, daysAgo: Int = 0): List<Balance> = balanceRepository.getBalance(origins, daysAgo)

    fun getDelta(origins: Collection<String>, daysAgo: Int = 7): List<DeltaBalance> {
        val delta = balanceRepository.getDelta(origins, daysAgo)
        val usdToEuro = quoteService.usdToEur()
        val quotes = quoteService[COINMARKETCAP]
        return quotes?.let { q ->
            usdToEuro?.let { u2e ->
                populateDeltaBalances(delta, q, u2e)
            }
        } ?: emptyList()
    }

    private fun populateDeltaBalances(
        delta: List<Balance>,
        quotes: Quotes,
        usdToEuro: BigDecimal
    ): List<DeltaBalance> {
        return delta.map {
            val valueMap = quotes[it.currency]?.price?.let { priceUSD ->
                val valueUSD = priceUSD.multiply(it.amount)
                FiatMap.of(
                    Fiat.USD to valueUSD,
                    Fiat.EUR to valueUSD.multiply(usdToEuro)
                )
            }
            DeltaBalance(it.currency, it.amount, valueMap)
        }
    }
}