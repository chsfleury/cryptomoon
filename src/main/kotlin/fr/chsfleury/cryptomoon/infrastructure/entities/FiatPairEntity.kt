package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table

object FiatPairEntity : Table("fiat_pairs") {
    val fiat = varchar("fiat", 10)
    val rate = decimal("rate", 10, 4)

    override val primaryKey = PrimaryKey(fiat)
}