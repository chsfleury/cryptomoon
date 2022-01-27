package fr.chsfleury.cryptomoon.domain.trigger

import fr.chsfleury.cryptomoon.domain.repository.TriggerRepository
import java.time.Duration
import java.time.Instant

abstract class Trigger(
    private val triggerName: String,
    val delay: Duration,
    private val after: List<Trigger> = emptyList()
) {
    abstract fun execute()

    fun trigger(triggerRepository: TriggerRepository, now: Instant, force: Boolean = false) {
        if (force || isExpired(triggerRepository, now)) {
            execute()
            updateTrigger(triggerRepository, now)
            after.forEach { next ->
                next.trigger(triggerRepository, now, true)
            }
        }
    }

    private fun isExpired(triggerRepository: TriggerRepository, now: Instant): Boolean = triggerRepository.lastTriggered(triggerName)?.plus(delay)?.isBefore(now) ?: true
    private fun updateTrigger(triggerRepository: TriggerRepository, now: Instant) = triggerRepository.update(triggerName, now)
}