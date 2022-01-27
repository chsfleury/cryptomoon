package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.repository.TriggerRepository
import fr.chsfleury.cryptomoon.infrastructure.entities.TriggerEntity
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

object ExposedTriggerRepository: TriggerRepository {

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

    override fun update(name: String, now: Instant) {
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
}