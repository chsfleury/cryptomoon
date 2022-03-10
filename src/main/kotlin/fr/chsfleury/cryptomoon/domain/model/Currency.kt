package fr.chsfleury.cryptomoon.domain.model

open class Currency (
    val symbol: String,
    val fiat: Boolean,
    val stable: Boolean,
    val logoUrl: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Currency

        if (symbol != other.symbol) return false

        return true
    }

    override fun hashCode(): Int {
        return symbol.hashCode()
    }

    override fun toString() = symbol
}