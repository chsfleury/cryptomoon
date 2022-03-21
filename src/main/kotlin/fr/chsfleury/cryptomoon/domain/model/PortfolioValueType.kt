package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import java.math.BigDecimal

enum class PortfolioValueType(val findValue: (PortfolioStats) -> BigDecimal?) {
    CURRENT(PortfolioStats::totalUSD),
    ATH(PortfolioStats::athTotalUSD);
}