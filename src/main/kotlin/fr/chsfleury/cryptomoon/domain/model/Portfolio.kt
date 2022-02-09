package fr.chsfleury.cryptomoon.domain.model

class Portfolio(
    val name: String,
    val accounts: Set<AccountSnapshot>
) {
    private val merged = accounts.size == 1 && accounts.first().origin == AccountSnapshot.ALL

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Portfolio

        if (name != other.name) return false
        if (merged != other.merged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + merged.hashCode()
        return result
    }


}