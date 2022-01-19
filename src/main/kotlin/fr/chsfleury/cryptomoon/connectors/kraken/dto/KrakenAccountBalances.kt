package fr.chsfleury.cryptomoon.connectors.kraken.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class KrakenAccountBalances: HashMap<String, String>()