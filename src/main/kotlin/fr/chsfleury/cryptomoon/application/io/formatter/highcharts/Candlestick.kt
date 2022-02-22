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

        if (min == null || snapshot.amount < min!!.amount) {
            min = snapshot
        }

        if (max == null || snapshot.amount > max!!.amount) {
            max = snapshot
        }
    }

    fun adjustMinMaxWithBounds() {
        if (open!!.amount > max!!.amount) {
            max = open
        }

        if (open!!.amount < min!!.amount) {
            min = open
        }

        if (close!!.amount > max!!.amount) {
            max = close
        }

        if (close!!.amount < min!!.amount) {
            min = close
        }
    }

    fun toJson() = listOf(at, open?.amount, max?.amount, min?.amount, close?.amount)
    override fun toString(): String {
        return "(open=$open, min=$min, max=$max, close=$close) @ " + Instant.ofEpochMilli(at).toString()
    }
}