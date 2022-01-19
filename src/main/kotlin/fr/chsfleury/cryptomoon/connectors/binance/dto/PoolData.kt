package fr.chsfleury.cryptomoon.connectors.binance.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoolData(
    val share: PoolShare
)
