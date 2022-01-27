package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.infrastructure.repository.execAndMap
import java.math.BigDecimal
import java.time.Instant

object ExposedFiatPairRepository {

    private const val EUR_USD_QUERY = """
    SELECT at, ratio
    FROM (
        SELECT
            q1.at,
            row_number() over (partition by q1.fiat, q1.at order by ABS(TIME_TO_SEC(TIMEDIFF(q1.at, q2.at))) ) nb,
            q1.amount / q2.amount ratio
        FROM quotes q1
        JOIN quotes q2
        ON ABS(TIME_TO_SEC(TIMEDIFF(q1.at, q2.at))) < 60
        WHERE q1.currency = 'BTC'
           AND q2.currency = 'BTC'
           AND q1.fiat = 'EUR'
           AND q2.fiat = 'USD'
     ) a WHERE a.nb = 1;
    """

    fun getUsdInEuros(): List<Pair<Instant, BigDecimal>> {
        return EUR_USD_QUERY.execAndMap {
            it.getTimestamp("at").toInstant() to it.getBigDecimal("ratio")
        }
    }

}