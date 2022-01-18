package fr.chsfleury.cryptomoon.utils

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Feign
import feign.Logger
import feign.http2client.Http2Client
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import java.net.http.HttpClient
import kotlin.reflect.KClass

object ClientFactory {

    fun <T : Any> create(cls: KClass<T>, baseUrl: String, http: HttpClient, mapper: ObjectMapper = Json): T {
        return Feign.builder()
            .client(Http2Client(http))
            .encoder(JacksonEncoder(mapper))
            .decoder(JacksonDecoder(mapper))
            .logger(Slf4jLogger(cls.java))
            .logLevel(Logger.Level.FULL)
            .target(cls.java, baseUrl)
    }

}