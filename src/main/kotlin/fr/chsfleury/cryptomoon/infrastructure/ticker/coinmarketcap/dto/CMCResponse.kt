package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CMCResponse<T: Any>(
    val data: T
)