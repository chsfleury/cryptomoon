package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.PortfolioConfiguration

interface PortfolioRepository {

    fun loadConfiguration(): PortfolioConfiguration?

}