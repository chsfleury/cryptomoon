package fr.chsfleury.cryptomoon.application.io.formatter.highcharts.strategies

import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.Candlestick

interface ConnectBucketStrategy {

    fun connectBuckets(buckets: Map<Long, Candlestick>)

}