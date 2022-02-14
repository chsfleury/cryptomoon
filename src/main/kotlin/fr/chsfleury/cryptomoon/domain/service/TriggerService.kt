package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.repository.TriggerRepository
import fr.chsfleury.cryptomoon.domain.trigger.Trigger
import fr.chsfleury.cryptomoon.utils.GCD
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.concurrent.timer

class TriggerService(
    private val triggerRepository: TriggerRepository,
    vararg triggerArgs: Trigger
): Logging {
    private val log = logger()
    private val triggers: List<Trigger>
    private val minDelay: Duration
    private lateinit var timer: Timer

    init {
        triggers = triggerArgs.toList()
        val minDelayInSeconds = GCD.compute(triggers.map { it.delay.seconds.toInt() })
        minDelay = Duration.ofSeconds(minDelayInSeconds.toLong())
        log.info("trigger loop set to {} minutes", minDelay.toMinutes())
    }

    fun start(): TriggerService {
        timer = timer("main", true, 0L, minDelay.toMillis()) {
            val now = Instant.now()
            val triggeredAt = triggerRepository.lastTriggered()
            val triggerExecuted = triggers.asSequence()
                .filter { it.trigger(triggeredAt, now, false) }
                .map { it.triggerName }
                .partition { it in triggeredAt.keys }

            if (triggerExecuted.first.isNotEmpty()) {
                triggerRepository.update(triggerExecuted.first, now)
            }

            if (triggerExecuted.second.isNotEmpty()) {
                triggerRepository.insert(triggerExecuted.second, now)
            }
        }
        return this
    }

    fun stop() {
        timer.cancel()
    }
}