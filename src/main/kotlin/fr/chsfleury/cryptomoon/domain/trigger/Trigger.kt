package fr.chsfleury.cryptomoon.domain.trigger

import java.time.Duration
import java.time.Instant

abstract class Trigger(
    val triggerName: String,
    val delay: Duration,
    private val after: List<Trigger> = emptyList()
) {
    abstract fun execute()

    fun trigger(triggered: Map<String, Instant>, now: Instant, force: Boolean = false): Boolean {
        if (force || isExpired(triggered, now)) {
            execute()
            after.forEach { next ->
                next.trigger(triggered, now, true)
            }
            return true
        }
        return false
    }

    private fun isExpired(triggered: Map<String, Instant>, now: Instant): Boolean = triggered[triggerName]
        ?.plus(delay)
        ?.isBefore(now)
        ?: true
}