package fr.chsfleury.cryptomoon.infrastructure.ticker.abstractapi

import feign.Headers
import feign.QueryMap
import feign.RequestLine

@Headers(
    "Accept: application/json"
)
interface AbstractApiClient {

    @RequestLine("GET /v1/live/")
    fun rates(@QueryMap params: RateParams): AbstractApiResponse

}