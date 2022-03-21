package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.TickerConfiguration

interface TickerRepository {

    val configuration: TickerConfiguration

}