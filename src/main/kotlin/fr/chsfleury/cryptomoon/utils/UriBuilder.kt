package fr.chsfleury.cryptomoon.utils

import java.net.URI

object UriBuilder {

    fun create(uri: String, queryParams: Map<String, String>): URI {
        val finalUri = if (queryParams.isNotEmpty()) {
            uri + '?' + joinQueryParams(queryParams)
        } else {
            uri
        }
        return URI.create(finalUri)
    }

    fun joinQueryParams(queryParams: Map<String, Any>): String = queryParams
        .map { it.key + '=' + it.value }
        .joinToString("&")

}