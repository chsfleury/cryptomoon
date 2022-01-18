package fr.chsfleury.cryptomoon.connectors.justmining.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetStakingsResponse(
    val data: List<StakingContract>
)