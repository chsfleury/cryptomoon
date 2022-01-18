package fr.chsfleury.cryptomoon.connectors.justmining.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class Masternode(
    val currencyCode: String,
    val collateral: BigDecimal,
    val reward: BigDecimal
)
