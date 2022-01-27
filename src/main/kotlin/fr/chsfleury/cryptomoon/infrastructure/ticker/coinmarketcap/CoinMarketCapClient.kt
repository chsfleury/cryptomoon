package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCListingLatestItem
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCListingLatestParams
import fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap.dto.CMCResponse

@Headers(
    "X-CMC_PRO_API_KEY: {apiKey}",
    "Accept: application/json"
)
interface CoinMarketCapClient {

    @RequestLine("GET /v1/cryptocurrency/listings/latest")
    fun listingLatest(@Param("apiKey") apiKey: String, @QueryMap params: CMCListingLatestParams): CMCResponse<List<CMCListingLatestItem>>

}