package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.*
import fr.chsfleury.cryptomoon.domain.repository.BalanceRepository
import java.math.BigDecimal
import java.math.RoundingMode

class BalanceService(
    private val currencyService: CurrencyService,
    private val balanceRepository: BalanceRepository
) {
    fun getBalance(origins: Collection<String>, daysAgo: Int = 0): List<Balance> = balanceRepository.getBalance(origins, daysAgo)

    fun getDelta(origins: Collection<String>, daysAgo: Int = 7): List<DeltaBalance> {
        val delta = balanceRepository.getDelta(origins, daysAgo)
        val quotesAndRates = currencyService.marketData()
        return populateDeltaBalances(delta, quotesAndRates)
    }

    private fun populateDeltaBalances(
        delta: List<Balance>,
        marketData: MarketData,
    ): List<DeltaBalance> {
        return delta.mapNotNull {
            when (it.currency) {
                Currencies.EUR.currency -> computeEuroValue(it.amount, marketData.rates[Fiat.EUR])
                Currencies.USD.currency -> it.amount
                else -> computeCryptoValue(marketData, it)
            }?.let { v -> DeltaBalance(it.currency, it.amount, v) }
        }
    }

    private fun computeEuroValue(
        balance: BigDecimal,
        usdToEuro: BigDecimal?
    ): BigDecimal? = if (usdToEuro == null) null else balance.divide(usdToEuro, RoundingMode.HALF_EVEN)

    private fun computeCryptoValue(
        marketData: MarketData,
        balance: Balance
    ): BigDecimal? {
        val priceUSD = marketData[balance.currency]?.priceUSD ?: return null
        return priceUSD.multiply(balance.amount)
    }
}