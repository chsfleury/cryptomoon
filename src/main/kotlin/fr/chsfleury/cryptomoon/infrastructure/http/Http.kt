package fr.chsfleury.cryptomoon.infrastructure.http

import java.net.http.HttpClient

object Http {
    val client: HttpClient = HttpClient.newHttpClient()
}