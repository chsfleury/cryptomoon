package fr.chsfleury.cryptomoon.infrastructure.dto

import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Currencies
import java.sql.ResultSet
import java.time.Instant

class BalanceRecord(resultSet: ResultSet) {
    val origin: String = resultSet.getString("origin")
    val balance: Balance = Balance(
        Currencies[resultSet.getString("currency")],
        resultSet.getBigDecimal("amount")
    )
    val timestamp: Instant = resultSet.getTimestamp("at").toInstant()
}
