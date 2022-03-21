package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.PortfolioValueType
import fr.chsfleury.cryptomoon.domain.repository.PortfolioHistoryRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.PortfolioHistoryEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit

object ExposedPortfolioHistoryRepository: PortfolioHistoryRepository {

    override fun insert(portfolioName: String, portfolioValueType: PortfolioValueType, portfolioValue: PorfolioValueSnapshot) {
        transaction {
            PortfolioHistoryEntity.insert {
                it[name] = portfolioName
                it[type] = portfolioValueType.name
                it[at] = portfolioValue.at
                it[value] = portfolioValue.valueUSD
            }
        }
    }

    override fun findBy(portfolioName: String, portfolioValueType: PortfolioValueType, days: Int): PortfolioHistory {
        val instantMin = Instant.now().minus(days.toLong(), ChronoUnit.DAYS)
        return transaction {
            val snapshots = PortfolioHistoryEntity.select {
                PortfolioHistoryEntity.type eq portfolioValueType.name and
                        (PortfolioHistoryEntity.at greaterEq instantMin)
            }.map {
                PorfolioValueSnapshot(
                    it[PortfolioHistoryEntity.at],
                    it[PortfolioHistoryEntity.value]
                )
            }
            PortfolioHistory(portfolioValueType, snapshots)
        }
    }
}