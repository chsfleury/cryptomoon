package fr.chsfleury.cryptomoon.domain.model

class Portfolio(
    val name: String,
    val accounts: Set<AccountSnapshot>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Portfolio

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}