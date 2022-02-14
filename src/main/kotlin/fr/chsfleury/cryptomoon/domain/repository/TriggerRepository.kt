package fr.chsfleury.cryptomoon.domain.repository

import java.time.Instant

interface TriggerRepository {

    fun lastTriggered(): Map<String, Instant>
    fun lastTriggered(name: String): Instant?
    fun upsert(name: String, now: Instant)
    fun update(names: Collection<String>, now: Instant)
    fun insert(names: Collection<String>, now: Instant)

}