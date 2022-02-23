package fr.chsfleury.cryptomoon.domain

import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

object Execution: Logging {
    private val log = logger()

    private var mode: Mode = Mode.STANDARD
        set(value) {
            field = value
            log.info("set execution mode to {}", value)
        }

    fun isStandard() = mode == Mode.STANDARD
    fun isReadonly() = mode == Mode.READONLY

    fun set(m: String?) {
        if (m != null) {
            mode = Mode.valueOf(m)
        }
    }

    enum class Mode {
        STANDARD,
        READONLY
    }
}