package fr.chsfleury.cryptomoon.application.io.formatter.highcharts.strategies

import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.Candlestick

object ConnectNeighborsBucketStragegy: ConnectBucketStrategy {

    override fun connectBuckets(buckets: Map<Long, Candlestick>) {
        var previousCandlestick: Candlestick? = null
        buckets.forEach { t, c ->
            if (previousCandlestick != null) {
                val pc = previousCandlestick!!
                val a = pc.close?.at?.toEpochMilli()?.let { t - it }
                val b = c.open?.at?.toEpochMilli()?.let { it - t }
                if (a != null && b != null) {
                    if (a < b) {
                        c.open = pc.close
                        c.adjustMinMaxWithBounds()
                    } else if (b < a) {
                        pc.close = c.open
                        c.adjustMinMaxWithBounds()
                    }
                }
            }
            previousCandlestick = c
        }
    }

}