package fr.chsfleury.cryptomoon.connectors.kraken

import feign.Body
import feign.Headers
import feign.Param
import feign.RequestLine
import fr.chsfleury.cryptomoon.connectors.kraken.dto.KrakenAccountBalances
import fr.chsfleury.cryptomoon.connectors.kraken.dto.KrakenResponse

@Headers(
    "Accept: application/json",
    "API-Key: {apiKey}",
    "API-Sign: {apiSign}",
    "Content-Type: application/x-www-form-urlencoded; charset=utf-8"
)
interface KrakenClient {
    companion object {
        const val USER_BALANCES_PATH = "/0/private/Balance"
    }

    @RequestLine("POST $USER_BALANCES_PATH")
    @Body("nonce={nonce}")
    fun userBalances(@Param("apiKey") apiKey: String, @Param("apiSign") apiSign: String, @Param("nonce") nonce: Long): KrakenResponse<KrakenAccountBalances>

}