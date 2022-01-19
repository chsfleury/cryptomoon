package fr.chsfleury.cryptomoon.connectors.binance

import fr.chsfleury.cryptomoon.utils.UriBuilder
import org.apache.commons.codec.binary.Hex
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.LinkedHashMap

object BinanceRequestHelper {
    private const val HMAC_SHA256 = "HmacSHA256"

    fun signRequest(queryMap: LinkedHashMap<String, Any>, secretKey: String) {
        queryMap["timestamp"] = System.currentTimeMillis()
        val queryData = UriBuilder.joinQueryParams(queryMap)
        queryMap["signature"] = getSignature(queryData, secretKey)
    }

    private fun getSignature(data: String, secretKey: String): String {
        val hmacSha256: ByteArray = try {
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), HMAC_SHA256)
            val mac = Mac.getInstance(HMAC_SHA256)
            mac.init(secretKeySpec)
            mac.doFinal(data.toByteArray())
        } catch (e: Exception) {
            throw RuntimeException("Failed to calculate hmac-sha256", e)
        }
        return Hex.encodeHexString(hmacSha256)
    }
}