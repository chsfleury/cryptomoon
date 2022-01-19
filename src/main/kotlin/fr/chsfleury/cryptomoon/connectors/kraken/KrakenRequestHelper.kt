package fr.chsfleury.cryptomoon.connectors.kraken

import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object KrakenRequestHelper {
    private const val HMAC_SHA_512 = "HmacSHA512"

    fun getSignature(uriPath: String, requestBody: String?, nonce: Long, secretKey: String): String {
        val decodedSecretKey = Base64.getDecoder().decode(secretKey.toByteArray())
        val secretKeySpec = SecretKeySpec(decodedSecretKey, HMAC_SHA_512)
        val mac512 = Mac.getInstance(HMAC_SHA_512)
        mac512.init(secretKeySpec)

        val body = requestBody ?: "nonce=$nonce"

        val sha256 = MessageDigest.getInstance("SHA-256")
        sha256.update((nonce.toString() + body).toByteArray())
        mac512.update(uriPath.toByteArray())
        mac512.update(sha256.digest())

        return Base64.getEncoder().encodeToString(mac512.doFinal()).trim()
    }

}