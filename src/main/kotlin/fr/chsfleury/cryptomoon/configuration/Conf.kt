package fr.chsfleury.cryptomoon.configuration

import com.uchuhimo.konf.ConfigSpec

object Conf: ConfigSpec("moon") {
    val port by optional(7777)
    val apiConfigFile by optional("config.json")
}