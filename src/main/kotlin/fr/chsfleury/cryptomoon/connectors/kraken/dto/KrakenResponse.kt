package fr.chsfleury.cryptomoon.connectors.kraken.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class KrakenResponse<T: Any>(
    val result: T
)