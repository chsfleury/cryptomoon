package fr.chsfleury.cryptomoon.connectors.binance.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoolShare(
    val asset: Map<String, String>
)
