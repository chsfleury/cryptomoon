package fr.chsfleury.cryptomoon.connectors.binance

import org.apache.commons.codec.binary.Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignatureGenerator {
    private const val HMAC_SHA256 = "HmacSHA256"

    fun getSignature(data: String, key: String): String {
        val hmacSha256: ByteArray = try {
            val secretKeySpec = SecretKeySpec(key.toByteArray(), HMAC_SHA256)
            val mac = Mac.getInstance(HMAC_SHA256)
            mac.init(secretKeySpec)
            mac.doFinal(data.toByteArray())
        } catch (e: Exception) {
            throw RuntimeException("Failed to calculate hmac-sha256", e)
        }
        return Hex.encodeHexString(hmacSha256)
    }
}