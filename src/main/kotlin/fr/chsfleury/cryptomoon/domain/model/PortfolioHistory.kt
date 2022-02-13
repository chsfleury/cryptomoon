package fr.chsfleury.cryptomoon.domain.model

class PortfolioHistory (
    val type: PortfolioValueType,
    val fiat: Fiat,
    val snapshots: List<PorfolioValueSnapshot>,
)