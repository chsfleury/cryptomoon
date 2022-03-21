package fr.chsfleury.cryptomoon.infrastructure.ticker.coinmarketcap

import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine

@Headers(
    "X-CMC_PRO_API_KEY: {apiKey}",
    "Accept: application/json"
)
interface CoinMarketCapClient {

    @RequestLine("GET /v1/cryptocurrency/listings/latest")
    fun listingLatest(@Param("apiKey") apiKey: String, @QueryMap params: CMCListingLatestParams): CMCResponse<List<CMCQuoteItem>>

    @RequestLine("GET /v1/cryptocurrency/quotes/latest")
    fun latestQuotes(@Param("apiKey") apiKey: String, @QueryMap params: CMCLatestQuotesParams): CMCResponse<Map<String, CMCQuoteItem>>
}