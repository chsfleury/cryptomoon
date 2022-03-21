package fr.chsfleury.cryptomoon.application.controller.utils

import fr.chsfleury.cryptomoon.domain.model.Fiat
import io.javalin.http.Context

object PathParams {

    fun Context.fiatPathParam(): Fiat = Fiat.values().firstOrNull { it.name == pathParam("fiat") } ?: Fiat.USD

}