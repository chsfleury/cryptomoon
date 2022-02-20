package fr.chsfleury.cryptomoon.application.controller

import fr.chsfleury.cryptomoon.domain.service.TriggerService
import io.javalin.http.Context

class TriggerController(
    private val triggerService: TriggerService
) {

    fun checkTriggers(ctx: Context) {
        val executed = triggerService.check()
        ctx.json(mapOf("executed" to executed))
    }

    fun forceTriggers(ctx: Context) {
        val executed = triggerService.check(true)
        ctx.json(mapOf("executed" to executed))
    }

}