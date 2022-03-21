package fr.chsfleury.cryptomoon.application.io.formatter.highcharts

import fr.chsfleury.cryptomoon.domain.model.PorfolioValueSnapshot
import java.time.Instant

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

        if (min == null || snapshot.valueUSD < min!!.valueUSD) {
            min = snapshot
        }

        if (max == null || snapshot.valueUSD > max!!.valueUSD) {
            max = snapshot
        }
    }

    fun adjustMinMaxWithBounds() {
        if (open!!.valueUSD > max!!.valueUSD) {
            max = open
        }

        if (open!!.valueUSD < min!!.valueUSD) {
            min = open
        }

        if (close!!.valueUSD > max!!.valueUSD) {
            max = close
        }

        if (close!!.valueUSD < min!!.valueUSD) {
            min = close
        }
    }

    fun toJson() = listOf(at, open?.valueUSD, max?.valueUSD, min?.valueUSD, close?.valueUSD)
    override fun toString(): String {
        return "(open=$open, min=$min, max=$max, close=$close) @ " + Instant.ofEpochMilli(at).toString()
    }
}