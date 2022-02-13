package fr.chsfleury.cryptomoon.domain.model

enum class Fiat {
    USD,
    EUR;

    companion object {
        operator fun get(name: String) = valueOf(name.uppercase())
    }
}