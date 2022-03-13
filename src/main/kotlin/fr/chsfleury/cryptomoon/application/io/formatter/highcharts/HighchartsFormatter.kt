package fr.chsfleury.cryptomoon.application.io.formatter.highcharts

import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.strategies.ConnectNeighborsBucketStragegy
import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.utils.MinMax.minMaxOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*


object HighchartsFormatter: ChartDataFormatter {

    private const val HOUR_IN_MILLIS = 3_600_000
    override val formatName = "highcharts"

    override fun assetDistributionData(portfolioStats: PortfolioStats, fiat: Fiat): List<Point> = portfolioStats.mergedAccountStats.assetsByValueDesc
        .map { Point(it.currency.symbol, it.value[fiat]?.toDouble() ?: 0.0) }

    override fun accountValueDistribution(portfolioStats: PortfolioStats, fiat: Fiat): List<Point> = portfolioStats.accountStats.asSequence()
        .filter { it.total[fiat] != null }
        .map { Point(it.origin, it.total.clean()[fiat]!!.toDouble()) }
        .sortedByDescending { it.y }
        .toList()

    override fun valueHistory(portfolioHistory: PortfolioHistory): List<List<Any?>> {
        if (portfolioHistory.snapshots.isEmpty()) {
            return emptyList()
        }

        val (minInstant, maxInstant) = portfolioHistory.snapshots.minMaxOf { it.at } ?: error("unexpected")
        val buckets = createBuckets(minInstant, maxInstant)
        portfolioHistory.snapshots.forEach { snapshot ->
            val key = toDateAndHour(snapshot.at)
            buckets[key]?.add(snapshot)
        }

        ConnectNeighborsBucketStragegy.connectBuckets(buckets)

        return buckets.map { it.value.toJson() }
    }

    override fun athHistory(portfolioHistory: PortfolioHistory): Any? {
        return portfolioHistory.snapshots.map { listOf(it.at.toEpochMilli(), it.amount) }
    }

    private fun createBuckets(minInstant: Instant, maxInstant: Instant): Map<Long, Candlestick> {
        val max = toDateAndHour(maxInstant)
        val buckets = TreeMap<Long, Candlestick>()
        var bucketKey = toDateAndHour(minInstant)
        while (bucketKey <= max) {
            buckets[bucketKey] = Candlestick(bucketKey)
            bucketKey += HOUR_IN_MILLIS
        }
        return buckets
    }

    private fun toDateAndHour(instant: Instant): Long {
        val dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        return 1000 * LocalDateTime.of(
            dateTime.toLocalDate(),
            LocalTime.of(dateTime.hour, 0)
        ).toEpochSecond(ZoneOffset.UTC)
    }
}