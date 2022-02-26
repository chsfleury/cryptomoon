package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.repository.BalanceRepository
import fr.chsfleury.cryptomoon.infrastructure.dto.BalanceRecord
import fr.chsfleury.cryptomoon.infrastructure.repository.execAndMap
import org.jetbrains.exposed.sql.transactions.transaction

object ExposedBalanceRepository : BalanceRepository {

    private const val ALL_BALANCES_QUERY = """
        SELECT sub.currency, sum(sub.amount), max(sub.at)
        FROM (
            SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC) rownumber
            FROM balances
            WHERE at < DATE_SUB(NOW(), INTERVAL %d DAY)
        ) sub
        WHERE rownumber = 1 AND sub.origin IN (%s)
        GROUP BY currency;
        """

    private const val DELTA_BALANCES_QUERY = """
        SELECT today.currency as currency, today.balance - past.balance as amount, today.at as at
        FROM (
                 SELECT sub.currency as currency, sum(sub.amount) balance, max(sub.at) at
                 FROM (
                          SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
                          FROM balances
                      ) sub
                 WHERE rownumber = 1 AND sub.origin IN (%s)
                 GROUP BY currency
        ) today
        JOIN
        (
            SELECT sub.currency, sum(sub.amount) balance, max(sub.at)
            FROM (
                     SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
                     FROM balances
                     WHERE at < DATE_SUB(NOW(), INTERVAL %d DAY)
                 ) sub
            WHERE rownumber = 1 AND sub.origin IN (%s)
            GROUP BY currency
        ) past ON today.currency = past.currency
        WHERE abs(today.balance - past.balance) > 0.0001;
    """

    override fun getBalance(origins: Collection<String>, daysAgo: Int): List<Balance> {
        return queryAndMap(ALL_BALANCES_QUERY.format(daysAgo, originIn(origins)))
    }

    override fun getDelta(origins: Collection<String>, daysAgo: Int): List<Balance> {
        val originIn = originIn(origins)
        return queryAndMap(DELTA_BALANCES_QUERY.format(originIn, daysAgo, originIn))
    }

    private fun queryAndMap(query: String): List<Balance> {
        return transaction {
            query.execAndMap {
                val record = BalanceRecord(it)
                record.balance
            }
        }
    }

    private fun originIn(origins: Collection<String>) = origins.joinToString("','", "'", "'")
}