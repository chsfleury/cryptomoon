package fr.chsfleury.cryptomoon.infrastructure.connector.justmining

import feign.Headers
import feign.Param
import feign.RequestLine
import fr.chsfleury.cryptomoon.infrastructure.connector.justmining.dto.GetMasternodesResponse
import fr.chsfleury.cryptomoon.infrastructure.connector.justmining.dto.GetStakingsResponse


@Headers(
    "Accept: application/json",
    "API-KEY: {apiKey}"
)
interface JustMiningClient {

    @RequestLine("GET /v1/stakings")
    fun stakingBalances(@Param("apiKey") apiKey: String): GetStakingsResponse

    @RequestLine("GET /v1/masternodes")
    fun masternodeBalances(@Param("apiKey") apiKey: String): GetMasternodesResponse

}