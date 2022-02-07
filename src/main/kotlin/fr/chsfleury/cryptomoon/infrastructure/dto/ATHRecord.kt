package fr.chsfleury.cryptomoon.infrastructure.dto

import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import java.math.BigDecimal
import java.sql.ResultSet

class ATHRecord(resultSet: ResultSet) {
    val currency: Currency = Currencies[resultSet.getString("currency")]
    val price: BigDecimal = resultSet.getBigDecimal("price")
}