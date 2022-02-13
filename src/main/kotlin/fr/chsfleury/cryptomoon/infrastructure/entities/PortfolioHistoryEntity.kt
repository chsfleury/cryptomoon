package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PortfolioHistoryEntity: Table("portfolio_history") {
    val name = varchar("name", 50)
    val type = varchar("type", 20)
    val fiat = varchar("fiat", 4)
    val at = timestamp("at")
    val value = decimal("amount", 12, 2)

    override val primaryKey = PrimaryKey(arrayOf(name, type, at, fiat))
}