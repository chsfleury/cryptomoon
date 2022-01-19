package fr.chsfleury.cryptomoon.connectors.binance

import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine
import fr.chsfleury.cryptomoon.connectors.binance.dto.AccountInfos
import fr.chsfleury.cryptomoon.connectors.binance.dto.PoolData
import javax.ws.rs.QueryParam

@Headers(
    "X-MBX-APIKEY: {apiKey}",
    "Content-Type: application/x-www-form-urlencoded"
)
interface BinanceClient {

    @RequestLine("GET /api/v3/account")
    fun accountData(
        @Param("apiKey") apiKey: String,
        @QueryMap queryParams: Map<String, Any>
    ): AccountInfos

    @RequestLine("GET /sapi/v1/bswap/liquidity")
    fun liquidityPoolData(
        @Param("apiKey") apiKey: String,
        @QueryMap queryParams: Map<String, Any>
    ): List<PoolData>

    @RequestLine("GET /sapi/v1/lending/union/account")
    fun stakingData(
        @Param("apiKey") apiKey: String,
        @QueryMap queryParams: Map<String, Any>
    ): List<Map<String, Any>>
}