package fr.chsfleury.cryptomoon.model

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class CustomConnector(
    private val http: HttpClient
): ApiConnector {
    abstract fun decorateRequest(req: HttpRequest.Builder)
    fun send(req: HttpRequest.Builder): HttpResponse<String> {
        decorateRequest(req)
        return http.send(req.build(), HttpResponse.BodyHandlers.ofString())
    }
}