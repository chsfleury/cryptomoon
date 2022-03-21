package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object QuoteEntity : Table("quotes") {
    val currency = varchar("currency", 10)
    val at = timestamp("at")
    val price = decimal("amount", 18, 8)
    val rank = integer("rank")

    override val primaryKey = PrimaryKey(arrayOf(currency, at))
}