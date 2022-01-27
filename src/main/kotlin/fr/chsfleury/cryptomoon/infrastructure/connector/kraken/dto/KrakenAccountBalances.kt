package fr.chsfleury.cryptomoon.infrastructure.connector.kraken.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class KrakenAccountBalances: HashMap<String, String>()