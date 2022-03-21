package fr.chsfleury.cryptomoon.domain.model

class PortfolioHistory (
    val type: PortfolioValueType,
    val snapshots: List<PorfolioValueSnapshot>,
)