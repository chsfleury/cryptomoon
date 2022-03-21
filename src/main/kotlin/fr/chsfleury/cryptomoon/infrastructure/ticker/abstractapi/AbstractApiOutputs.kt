package fr.chsfleury.cryptomoon.infrastructure.ticker.abstractapi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class AbstractApiResponse(
    val base: String,
    @JsonProperty("exchange_rates") val rates: Map<String, BigDecimal>
)