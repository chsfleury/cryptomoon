package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.repository.TriggerRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.TriggerEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object ExposedTriggerRepository: TriggerRepository {

    override fun lastTriggered(): Map<String, Instant> {
        return transaction {
            TriggerEntity.selectAll()
                .asSequence()
                .map { row -> row[TriggerEntity.name] to row[TriggerEntity.at] }
                .toMap()
        }
    }

    override fun lastTriggered(name: String): Instant? {
        var lastTriggered: Instant? = null
        transaction {
            lastTriggered = TriggerEntity
                .slice(TriggerEntity.at)
                .select { TriggerEntity.name eq name }
                .limit(1)
                .firstOrNull()
                ?.get(TriggerEntity.at)
        }
        return lastTriggered
    }

    override fun upsert(name: String, now: Instant) {
        transaction {
            val lastTriggered = TriggerEntity
                .select { TriggerEntity.name eq name }
                .limit(1)
                .firstOrNull()
            if (lastTriggered == null) {
                TriggerEntity.insert {
                    it[TriggerEntity.name] = name
                    it[at] = Instant.now()
                }
            } else {
                TriggerEntity.update({ TriggerEntity.name eq name }) {
                    it[at] = now
                }
            }
        }
    }

    override fun update(names: Collection<String>, now: Instant) {
        transaction {
            TriggerEntity.update({ TriggerEntity.name inList names }) {
                it[at] = now
            }
        }
    }

    override fun insert(names: Collection<String>, now: Instant) {
        transaction {
            TriggerEntity.batchInsert(names) {
                this[TriggerEntity.name] = it
                this[TriggerEntity.at] = now
            }
        }
    }
}