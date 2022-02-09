package fr.chsfleury.cryptomoon.application.highcharts

import fr.chsfleury.cryptomoon.domain.model.Fiat
import fr.chsfleury.cryptomoon.domain.model.stats.AssetStats

object Highcharts {

    fun toData(assetStats: List<AssetStats>): List<Point> = assetStats.map { Point(it.currency.symbol, it.value[Fiat.USD]?.toDouble() ?: 0.0) }

}