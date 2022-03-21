package fr.chsfleury.cryptomoon.infrastructure.dto

import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.Instant

class QuoteRecord(resultSet: ResultSet) {
    val currency: Currency = Currencies[resultSet.getString("currency")]
    val timestamp: Instant = resultSet.getTimestamp("at").toInstant()
    val price: BigDecimal = resultSet.getBigDecimal("amount")
    val rank: Int = resultSet.getInt("rank")
}
