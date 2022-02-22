package fr.chsfleury.cryptomoon.domain

import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

object ExecutionMode:Logging {
    private val log = logger()
    const val READONLY = 0
    const val STANDARD = 1

    private var currentMode: Int = 1

    fun isStandard() = currentMode == STANDARD
    fun isReadonly() = currentMode == READONLY

    fun set(mode: Int?) {
        if (mode != null) {
            currentMode = when(mode) {
                0 -> {
                    log.info("set execution mode to readonly")
                    READONLY
                }
                else -> {
                    log.info("set execution mode to standard")
                    STANDARD
                }
            }
        }
    }
}