package fr.chsfleury.cryptomoon.connectors.justmining.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class StakingContract(
    val currencyCode: String,
    val amount: BigDecimal,
    val reward: BigDecimal
)