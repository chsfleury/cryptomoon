package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.MarketData
import fr.chsfleury.cryptomoon.domain.model.Quote
import fr.chsfleury.cryptomoon.domain.repository.CurrencyDataRepository
import fr.chsfleury.cryptomoon.domain.repository.FiatPairRepository
import fr.chsfleury.cryptomoon.domain.repository.QuoteRepository
import fr.chsfleury.cryptomoon.utils.FiatMap
import java.math.BigDecimal

class CurrencyService(
    private val currencyDataRepository: CurrencyDataRepository,
    private val quoteRepository: QuoteRepository,
    private val fiatPairRepository: FiatPairRepository,
) {

    fun marketData() = MarketData(
        currencyDataRepository.all().sortedBy { it.rank },
        fiatPairRepository.all()
    )

    fun rates(): FiatMap = fiatPairRepository.all()

    fun usdToFiat(fiat: Fiat): BigDecimal? = fiatPairRepository.getUsdToFiat(fiat)

    // W

    fun saveQuote(quotes: List<Quote>) {
        quoteRepository.save(quotes)
        currencyDataRepository.saveQuote(quotes)
    }

    fun saveRate(rates: FiatMap) {
        fiatPairRepository.save(rates)
    }

    fun saveATH(aths: Collection<ATH>) {
        currencyDataRepository.saveATH(aths)
    }
}