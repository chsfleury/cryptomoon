package fr.chsfleury.cryptomoon.domain.repository

import java.time.Instant

interface TriggerRepository {

    fun lastTriggered(name: String): Instant?
    fun update(name: String, now: Instant)

}