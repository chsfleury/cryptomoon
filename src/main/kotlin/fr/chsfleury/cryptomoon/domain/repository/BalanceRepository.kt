package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.Balance

interface BalanceRepository {

    fun getBalance(origins: Collection<String>, daysAgo: Int = 0): List<Balance>
    fun getDelta(origins: Collection<String>, daysAgo: Int = 7): List<Balance>

}