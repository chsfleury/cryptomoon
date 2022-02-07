package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto

class CoinSingleRequest(
    val currency: String,
    val code: String,
    val meta: Boolean
)