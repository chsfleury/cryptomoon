package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table

object ATHEntity : Table("ath") {
    val currency = varchar("currency", 10)
    val price = decimal("price", 18, 8)

    override val primaryKey = PrimaryKey(currency)
}