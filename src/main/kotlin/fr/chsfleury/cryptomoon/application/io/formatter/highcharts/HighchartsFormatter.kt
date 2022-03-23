package fr.chsfleury.cryptomoon.application.io.formatter.highcharts

import fr.chsfleury.cryptomoon.application.io.BigDecimals.applyRate
import fr.chsfleury.cryptomoon.application.io.BigDecimals.clean
import fr.chsfleury.cryptomoon.application.io.formatter.ChartDataFormatter
import fr.chsfleury.cryptomoon.application.io.formatter.highcharts.strategies.ConnectNeighborsBucketStragegy
import fr.chsfleury.cryptomoon.domain.model.PortfolioHistory
import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import fr.chsfleury.cryptomoon.utils.MinMax.minMaxOf
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*


object HighchartsFormatter: ChartDataFormatter {

    private const val BUCKET_SIZE_IN_SECONDS = 1_200 // 20min
    private const val BUCKET_SIZE_IN_MILLIS = BUCKET_SIZE_IN_SECONDS * 1000
    override val formatName = "highcharts"

    override fun assetDistributionData(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): List<Point> = portfolioStats.mergedAccountStats.assetsByValueDesc
        .map { Point(it.currency.symbol, it.valueUSD.applyRate(conversionRate).toDouble()) }

    override fun accountValueDistribution(portfolioStats: PortfolioStats, conversionRate: BigDecimal?): List<Point> = portfolioStats.accountStats.asSequence()
        .map { Point(it.origin, it.total.applyRate(conversionRate).clean().toDouble()) }
        .sortedByDescending { it.y }
        .toList()

    override fun valueHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): List<List<Any?>> {
        if (portfolioHistory.snapshots.isEmpty()) {
            return emptyList()
        }

        val (minInstant, maxInstant) = portfolioHistory.snapshots.minMaxOf { it.at } ?: error("unexpected")
        val buckets = createBuckets(minInstant, maxInstant)
        portfolioHistory.snapshots.forEach { snapshot ->
            val key = toBucketKey(snapshot.at)
            buckets[key]?.add(snapshot.applyRate(conversionRate))
        }

        ConnectNeighborsBucketStragegy.connectBuckets(buckets)

        return buckets.map { it.value.toJson() }
    }

    override fun athHistory(portfolioHistory: PortfolioHistory, conversionRate: BigDecimal?): Any {
        return portfolioHistory.snapshots.map { listOf(it.at.toEpochMilli(), it.valueUSD.applyRate(conversionRate)) }
    }

    private fun createBuckets(minInstant: Instant, maxInstant: Instant): Map<Long, Candlestick> {
        val max = toBucketKey(maxInstant)
        val buckets = TreeMap<Long, Candlestick>()
        var bucketKey = toBucketKey(minInstant)
        while (bucketKey <= max) {
            buckets[bucketKey] = Candlestick(bucketKey)
            bucketKey += BUCKET_SIZE_IN_MILLIS
        }
        return buckets
    }

    private fun toBucketKey(instant: Instant): Long {
        val dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
        val secondsOfDay = dateTime.toLocalTime().toSecondOfDay()
        val truncatedSecondOfDay = (secondsOfDay / BUCKET_SIZE_IN_SECONDS) * BUCKET_SIZE_IN_SECONDS
        return 1000 * LocalDateTime.of(
            dateTime.toLocalDate(),
            LocalTime.ofSecondOfDay(truncatedSecondOfDay.toLong())
        ).toEpochSecond(ZoneOffset.UTC)
    }
}