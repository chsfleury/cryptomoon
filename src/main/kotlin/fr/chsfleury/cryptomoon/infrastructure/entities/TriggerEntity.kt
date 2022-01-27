package fr.chsfleury.cryptomoon.infrastructure.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object TriggerEntity : Table("triggers") {
    val name = varchar("name", 20)
    val at = timestamp("at")

    override val primaryKey = PrimaryKey(name)
}