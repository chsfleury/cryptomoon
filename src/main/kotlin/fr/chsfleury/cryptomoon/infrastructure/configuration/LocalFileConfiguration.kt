package fr.chsfleury.cryptomoon.infrastructure.configuration

import fr.chsfleury.cryptomoon.utils.Json
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.nio.file.Files
import java.nio.file.Paths

object LocalFileConfiguration: Logging {
    val log = logger()
    val root: Map<*, *>

    init {
        val currentDirectory = Paths.get(System.getProperty("user.dir"))
        log.info("Current Directory: {}", currentDirectory)
        val configFileContent = Files.readAllBytes(currentDirectory.resolve("config/config.json"))
        root = Json.readValue(configFileContent, Map::class.java) as Map<*, *>
    }

    operator fun <T> get(key: String): T? {
        return root[key] as? T
    }

    operator fun <T> get(vararg keys: String): T? {
        var ite: Map<*, *>? = root
        keys.forEachIndexed { i, key ->
            if (ite != null) {
                if (i < keys.size - 1) {
                    ite = ite!![key] as? Map<*, *>
                } else {
                    return ite!![key] as? T
                }
            }
        }
        return null
    }

    fun <T> require(key: String): T {
        return get(key) ?: error("unknown key '$key'")
    }

    fun <T> require(vararg keys: String): T {
        return get(*keys) ?: error("unknown key path '${keys.joinToString(".") }'")
    }
}