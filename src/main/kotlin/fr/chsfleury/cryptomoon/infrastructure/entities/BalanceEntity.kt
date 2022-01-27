package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object BalanceEntity : Table("balances") {
    val currency = varchar("currency", 10)
    val origin = varchar("origin", 50)
    val at = timestamp("at")
    val amount = decimal("amount", 27, 18)

    override val primaryKey = PrimaryKey(arrayOf(currency, origin, at))
}