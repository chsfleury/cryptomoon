package fr.chsfleury.cryptomoon.infrastructure.connector.kraken.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KrakenResponse<T: Any>(
    val result: T
)