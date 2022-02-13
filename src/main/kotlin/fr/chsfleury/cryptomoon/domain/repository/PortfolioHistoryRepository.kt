package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType

interface PortfolioHistoryRepository {

    fun insert(portfolioName: String, portfolioValueType: PortfolioValueType, portfolioValue: PorfolioValueSnapshot)
    fun findBy(portfolioName: String, portfolioValueType: PortfolioValueType, fiat: Fiat, days: Int): PortfolioHistory

}