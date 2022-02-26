package fr.chsfleury.cryptomoon.infrastructure.repository.exposed

import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Balance
import fr.chsfleury.cryptomoon.domain.model.Currencies
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository
import fr.chsfleury.cryptomoon.infrastructure.dto.PlatformBalanceRecord
import fr.chsfleury.cryptomoon.infrastructure.dto.MutableAccount
import fr.chsfleury.cryptomoon.infrastructure.entities.BalanceEntity
import fr.chsfleury.cryptomoon.infrastructure.repository.execAndMap
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object ExposedAccountRepository: AccountRepository {
    private const val ALL_BALANCES_QUERY = """
        SELECT sub.currency, sum(sub.amount) sum, min(sub.at) minAt
        FROM (
             SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
             FROM balances
        ) sub
        WHERE rownumber = 1 AND origin IN %s
        GROUP BY currency
        HAVING sum > 0;
        """

    private const val PLATFORM_BALANCES_QUERY = """
        SELECT sub.currency, sub.amount, sub.at
        FROM (
             SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
             FROM balances
        ) sub
        WHERE rownumber = 1
        AND origin = '%s';
    """

    private const val ALL_PLATFORM_BALANCES_QUERY = """
        SELECT sub.currency, sub.amount, sub.origin, sub.at
        FROM (
             SELECT *, ROW_NUMBER() over (PARTITION BY currency, origin ORDER BY at DESC ) rownumber
             FROM balances
        ) sub
        WHERE rownumber = 1;
    """

    override fun mergedAccounts(origins: Set<String>): AccountSnapshot {
        if (origins.isEmpty()) {
            return AccountSnapshot(AccountSnapshot.ALL, emptySet(), Instant.now())
        }

        var minTimestamp = Instant.now()
        return transaction {
            var inClause = origins.joinToString(",") { "'$it'" }
            inClause = "($inClause)"
            val balanceList = ALL_BALANCES_QUERY.format(inClause).execAndMap {
                val minAt = it.getTimestamp("minAt").toInstant()
                if (minAt.isBefore(minTimestamp)) {
                    minTimestamp = minAt
                }
                Balance(
                    Currencies[it.getString("currency")],
                    it.getBigDecimal("sum")
                )
            }

            AccountSnapshot.of(AccountSnapshot.ALL, minTimestamp, balanceList)
        }
    }

    override fun allAccounts(): Set<AccountSnapshot> {
        val collectingMap = mutableMapOf<String, MutableAccount>()
        val balances: List<PlatformBalanceRecord> = transaction { ALL_PLATFORM_BALANCES_QUERY.execAndMap(::PlatformBalanceRecord) }

        balances.forEach { record ->
            val account = collectingMap.computeIfAbsent(record.origin) { origin ->
                MutableAccount(origin, mutableListOf(), Instant.now())
            }

            if (record.timestamp.isBefore(account.timestamp)) {
                account.timestamp = record.timestamp
            }

            account.balances.add(record.balance)
        }

        return collectingMap.mapTo(mutableSetOf()) { it.value.toAccount() }
    }

    override fun getAccount(origin: String): AccountSnapshot? {
        var minTimestamp = Instant.now()
        return transaction {
            val balanceList = PLATFORM_BALANCES_QUERY.format(origin).execAndMap {
                val minAt = it.getTimestamp("at").toInstant()
                if (minAt.isBefore(minTimestamp)) {
                    minTimestamp = minAt
                }
                Balance(
                    Currencies[it.getString("currency")],
                    it.getBigDecimal("amount")
                )
            }

            if (balanceList.isNotEmpty()) {
                AccountSnapshot.of(origin, minTimestamp, balanceList)
            } else {
                null
            }
        }
    }

    override fun getKnownCurrencies(filterFiat: Boolean): Set<Currency> {
        return transaction {
            var seq = BalanceEntity
                .slice(BalanceEntity.currency)
                .selectAll()
                .withDistinct(true)
                .asSequence()
                .map { Currencies[it[BalanceEntity.currency]] }

            if (filterFiat) {
                seq = seq.filterNot { it.fiat }
            }

            seq.toSet()
        }
    }

    override fun insert(account: AccountSnapshot) {
        transaction {
            BalanceEntity.batchInsert(account.balances) {
                this[BalanceEntity.currency] = it.currency.symbol
                this[BalanceEntity.origin] = account.origin
                this[BalanceEntity.at] = account.timestamp
                this[BalanceEntity.amount] = it.amount
            }
        }
    }

}