package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.ATH
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.repository.ATHRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.ATHEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object ExposedATHRepository: ATHRepository {
    override fun getATHs(): Set<ATH> {
        return transaction {
            ATHEntity.selectAll()
                .mapTo(mutableSetOf()) {
                    ATH(
                        Currencies[it[ATHEntity.currency]],
                        it[ATHEntity.price]
                    )
                }
        }
    }

    override fun save(ath: ATH) {
        transaction {
            val existingATH = ATHEntity
                .select { ATHEntity.currency eq ath.currency.symbol }
                .count() > 0

            if (existingATH) {
                ATHEntity.update({ ATHEntity.currency eq ath.currency.symbol }) {
                    it[price] = ath.price
                }
            } else {
                ATHEntity.insert {
                    it[currency] = ath.currency.symbol
                    it[price] = ath.price
                }
            }
        }
    }

    override fun save(aths: Collection<ATH>) {
        transaction {
            val existingAths = getATHs()
            val athMap = aths.groupBy { it in existingAths }
            val toInsert = athMap[false] ?: emptyList()
            val toUpdate = athMap[true] ?: emptyList()

            ATHEntity.batchInsert(toInsert, false) {
                this[ATHEntity.currency] = it.currency.symbol
                this[ATHEntity.price] = it.price
            }

            toUpdate.forEach { ath ->
                ATHEntity.update({ ATHEntity.currency eq ath.currency.symbol }) {
                    it[price] = ath.price
                }
            }
        }
    }

}