package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal

object CurrencyEntity : Table("currencies") {
    val currency = varchar("currency", 10)
    val price = decimal("price_usd", 18, 8).default(BigDecimal.ZERO)
    val ath = decimal("ath_usd", 18, 8).default(BigDecimal.ZERO)
    val rank = integer("rank").default(Int.MAX_VALUE)

    override val primaryKey = PrimaryKey(currency)
}