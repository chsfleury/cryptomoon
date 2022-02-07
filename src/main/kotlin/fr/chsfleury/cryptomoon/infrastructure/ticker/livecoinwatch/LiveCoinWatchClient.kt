package fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch

import feign.Body
import feign.Headers
import feign.Param
import feign.RequestLine
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto.CoinListRequest
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto.CoinResponseItem
import fr.chsfleury.cryptomoon.infrastructure.ticker.livecoinwatch.dto.CoinSingleRequest

@Headers(
    "x-api-key: {apiKey}",
    "Accept: application/json",
    "content-type: application/json"
)
interface LiveCoinWatchClient {

    @RequestLine("POST /coins/list")
    fun coinList(body: CoinListRequest, @Param("apiKey") apiKey: String): List<CoinResponseItem>

    @RequestLine("POST /coins/single")
    fun coinSingle(body: CoinSingleRequest, @Param("apiKey") apiKey: String): CoinResponseItem

}