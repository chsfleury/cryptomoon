package fr.chsfleury.cryptomoon.infrastructure.connector.binance.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoolShare(
    val asset: Map<String, String>
)
