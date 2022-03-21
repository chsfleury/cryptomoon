package fr.chsfleury.cryptomoon.application.io

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import fr.chsfleury.cryptomoon.domain.model.Fiat
import java.math.BigDecimal
import java.time.Instant

@JsonPropertyOrder("from", "to", "rate")
class FiatPairJson(
    val to: Fiat,
    val rate: BigDecimal
) {
    val from = Fiat.USD
}