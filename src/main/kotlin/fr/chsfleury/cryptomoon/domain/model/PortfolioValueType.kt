package fr.chsfleury.cryptomoon.domain.model

import fr.chsfleury.cryptomoon.domain.model.stats.PortfolioStats
import java.math.BigDecimal

enum class PortfolioValueType(val findValue: (PortfolioStats, Fiat) -> BigDecimal?) {
    CURRENT({ stats, fiat -> stats.total[fiat] }),
    ATH({ stats, fiat -> stats.athTotal[fiat] });
}