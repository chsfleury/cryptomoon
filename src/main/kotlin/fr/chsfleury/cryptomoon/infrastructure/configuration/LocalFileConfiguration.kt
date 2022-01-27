package fr.chsfleury.cryptomoon.infrastructure.configuration

import fr.chsfleury.cryptomoon.utils.Json
import java.nio.file.Files
import java.nio.file.Paths

object LocalFileConfiguration {
    val root: Map<*, *>

    init {
        val currentDirectory = Paths.get(System.getProperty("user.dir"))
        val configFileContent = Files.readAllBytes(currentDirectory.resolve("config.json"))
        root = Json.readValue(configFileContent, Map::class.java) as Map<*, *>
    }

    operator fun <T> get(key: String): T {
        return root[key] as? T ?: error("unknown key '$key'")
    }

    operator fun <T> get(vararg keys: String): T {
        var ite = root
        keys.forEachIndexed { i, key ->
            if (i < keys.size - 1) {
                ite = ite[key] as? Map<*, *> ?: error("unknown key '$key'")
            } else {
                return ite[key] as? T ?: error("unknown key '$key'")
            }
        }
        error("unexpected behavior")
    }
}