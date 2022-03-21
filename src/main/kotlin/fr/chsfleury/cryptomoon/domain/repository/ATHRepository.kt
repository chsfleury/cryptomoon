package fr.chsfleury.cryptomoon.domain.repository

import fr.chsfleury.cryptomoon.domain.model.impl.SimpleATH

interface ATHRepository {

    fun getATHs(): Set<SimpleATH>
    fun save(ath: SimpleATH)
    fun save(aths: Collection<SimpleATH>)

}