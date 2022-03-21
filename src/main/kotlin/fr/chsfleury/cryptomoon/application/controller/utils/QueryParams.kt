package fr.chsfleury.cryptomoon.application.controller.utils

import fr.chsfleury.cryptomoon.domain.model.Fiat
import io.javalin.http.Context

object QueryParams {

    fun Context.fiatQueryParam(): Fiat = Fiat.values().firstOrNull { it.name == queryParam("fiat")?.uppercase() } ?: Fiat.USD

}