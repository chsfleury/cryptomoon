package fr.chsfleury.cryptomoon.infrastructure.ticker.abstractapi

class RateParams(
    val api_key: String,
    val target: String = "EUR"
) {
    val base = "USD"
}

