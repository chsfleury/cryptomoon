package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.repository.FiatPairRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.FiatPairEntity
import fr.chsfleury.cryptomoon.utils.FiatMap
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object ExposedFiatPairRepository : FiatPairRepository {
    override fun all(): FiatMap {
        val all = FiatMap()
        transaction {
            FiatPairEntity.selectAll()
                .forEach { all[Fiat[it[FiatPairEntity.fiat]]] = it[FiatPairEntity.rate] }
        }
        return all
    }

    override fun getUsdToFiat(fiat: Fiat): BigDecimal? {
        if (fiat == Fiat.USD) {
            return BigDecimal.ONE
        }
        return transaction {
            val rate = FiatPairEntity
                .select { (FiatPairEntity.fiat eq fiat.name) }
                .map { it[FiatPairEntity.rate] }

            if (rate.isNotEmpty()) rate[0] else BigDecimal.ZERO
        }
    }

    override fun save(rates: FiatMap) {
        transaction {
            rates.forEach { (f: Fiat, r: BigDecimal?) ->
                if (r != null && exists(f)) {
                    FiatPairEntity.update ({ FiatPairEntity.fiat eq f.name }) {
                        it[rate] = r
                    }
                } else {
                    FiatPairEntity.insert {
                        it[fiat] = f.name
                        it[rate] = r
                    }
                }
            }
        }
    }

    private fun exists(fiat: Fiat): Boolean {
        return FiatPairEntity.select { FiatPairEntity.fiat eq fiat.name }
            .toList()
            .isNotEmpty()
    }

}