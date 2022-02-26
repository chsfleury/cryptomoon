package fr.chsfleury.cryptomoon.infrastructure.dto

import java.sql.ResultSet

class PlatformBalanceRecord(resultSet: ResultSet): BalanceRecord(resultSet) {
    val origin: String = resultSet.getString("origin")
}
