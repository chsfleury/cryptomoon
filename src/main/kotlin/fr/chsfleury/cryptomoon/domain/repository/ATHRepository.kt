package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.ATH

interface ATHRepository {

    fun getATHs(): Set<ATH>
    fun save(ath: ATH)
    fun save(aths: Collection<ATH>)

}