package fr.chsfleury.cryptomoon.infrastructure.ticker.messari

data class GetAssetMetricsParams(
    val fields: String?
)

data class AllAssetsParams(
    val fields: String,
    val limit: Int = 500,
    val page: Int = 1
)
