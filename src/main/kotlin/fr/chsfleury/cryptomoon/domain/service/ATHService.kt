package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.ATHRepository
import java.math.BigDecimal

class ATHService(
    private val athRepository: ATHRepository
) {
    private val aths: MutableMap<Currency, BigDecimal> = mutableMapOf()

    init {
        refresh()
    }

    operator fun get(currency: Currency): BigDecimal? = aths[currency]

    fun save(ath: ATH) = athRepository.save(ath)
    fun save(aths: Collection<ATH>) = athRepository.save(aths)

    fun refresh() {
        aths.clear()
        athRepository.getATHs().forEach {
            aths[it.currency] = it.price
        }
    }
}