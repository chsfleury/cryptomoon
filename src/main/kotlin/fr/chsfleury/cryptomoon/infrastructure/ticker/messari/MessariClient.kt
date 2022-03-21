package fr.chsfleury.cryptomoon.infrastructure.ticker.messari

import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine

@Headers(
    "x-messari-api-key: {apiKey}",
    "Accept: application/json"
)
interface MessariClient {

    @RequestLine("GET /api/v2/assets")
    fun allAssets(@Param("apiKey") apiKey: String, @QueryMap params: AllAssetsParams): MessariResponse<List<AllAssetsItem>>

}