package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.repository.TriggerRepository
import fr.chsfleury.cryptomoon.domain.trigger.Trigger
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timer

class TriggerService(
    private val triggerRepository: TriggerRepository,
    vararg triggerArgs: Trigger
): Logging {
    private val log = logger()
    private val triggers: List<Trigger>
    private lateinit var timer: Timer

    private val lastTriggeredCache = mutableMapOf<String, Instant>()
    private var lastTriggeredCacheExpiration: Instant = Instant.now()

    init {
        triggers = triggerArgs.toList()
    }

    fun start(): TriggerService {
        timer = timer("main", true, 0L, Duration.ofMinutes(2L).toMillis()) {
            log.debug("check triggers")
            val executed = check()
            if (executed.isNotEmpty()) {
                log.info("executed: {}", executed)
            }
        }
        return this
    }

    fun stop() {
        timer.cancel()
    }

    fun check(force: Boolean = false): Set<String> {
        val now = Instant.now()
        val triggeredAt = lastTriggered(now)
        val executedSet = mutableSetOf<String>()
        val triggerExecuted = triggers.asSequence()
            .filter { it.trigger(triggeredAt, now, force) }
            .map { it.triggerName }
            .onEach(executedSet::add)
            .partition { it in triggeredAt.keys }

        if (triggerExecuted.first.isNotEmpty()) {
            triggerExecuted.first.forEach { triggerName ->
                lastTriggeredCache[triggerName] = now
            }
            triggerRepository.update(triggerExecuted.first, now)
        }

        if (triggerExecuted.second.isNotEmpty()) {
            triggerExecuted.second.forEach { triggerName ->
                lastTriggeredCache[triggerName] = now
            }
            triggerRepository.insert(triggerExecuted.second, now)
        }

        return executedSet
    }

    private fun lastTriggered(now: Instant): Map<String, Instant> {
        return if (now < lastTriggeredCacheExpiration) {
            if (lastTriggeredCache.isEmpty()) {
                lastTriggeredCache += triggerRepository.lastTriggered()
            }
            lastTriggeredCache
        } else {
            lastTriggeredCache.clear()
            lastTriggeredCache += triggerRepository.lastTriggered()
            lastTriggeredCacheExpiration = now.plus(15L, ChronoUnit.MINUTES)
            lastTriggeredCache
        }
    }
}