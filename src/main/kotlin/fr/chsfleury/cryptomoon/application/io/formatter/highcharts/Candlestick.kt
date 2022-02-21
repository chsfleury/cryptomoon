package fr.chsfleury.cryptomoon.application.io.formatter.highcharts

import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import java.time.LocalDateTime
import java.time.ZoneOffset

class Candlestick (
    val at: Long
) {
    var open: PorfolioValueSnapshot? = null
    var min: PorfolioValueSnapshot? = null
    var max: PorfolioValueSnapshot? = null
    var close: PorfolioValueSnapshot? = null

    fun add(snapshot: PorfolioValueSnapshot) {
        if (open == null || snapshot.at < open!!.at) {
            open = snapshot
        }

        if (close == null || snapshot.at > close!!.at) {
            close = snapshot
        }

        if (min == null || snapshot.amount < min!!.amount) {
            min = snapshot
        }

        if (max == null || snapshot.amount > max!!.amount) {
            max = snapshot
        }
    }

    fun toJson() = listOf(at, open?.amount, max?.amount, min?.amount, close?.amount)
}