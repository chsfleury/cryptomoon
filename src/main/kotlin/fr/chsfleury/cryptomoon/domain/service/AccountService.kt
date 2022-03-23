package fr.chsfleury.cryptomoon.domain.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.common.base.Suppliers
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger
import java.time.Duration
import java.util.concurrent.TimeUnit

class AccountService(
    private val accountRepositories: Collection<AccountRepository>
) : Logging {
    private val log = logger()

    private var allAccountCache = Suppliers.memoizeWithExpiration(this::getAllAccounts, 3, TimeUnit.MINUTES)
    private val mergedAccountCache: LoadingCache<Set<String>, AccountSnapshot> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(3))
        .build { origins -> computeAccountMerge(origins) }

    fun allAccounts(): Set<AccountSnapshot> = allAccountCache.get()

    private fun getAllAccounts(): Set<AccountSnapshot> {
        log.debug("retrieve all accounts data")
        val accountSnapshots = accountRepositories.flatMapTo(mutableListOf(), AccountRepository::allAccounts)
        val collision = accountSnapshots.mapTo(mutableSetOf(), AccountSnapshot::origin).size < accountSnapshots.size
        return if (!collision) {
            accountSnapshots.toSet()
        } else {
            val snapshotMap = mutableMapOf<String, AccountSnapshot>()
            accountSnapshots.forEach { snap ->
                val existingSnap = snapshotMap[snap.origin]
                if (existingSnap == null) {
                    snapshotMap[snap.origin] = snap
                } else {
                    snapshotMap[snap.origin] = AccountSnapshot.merge(listOf(existingSnap, snap), snap.origin) ?: error("cannot happen theorically")
                }
            }
            snapshotMap.values.toSet()
        }
    }

    fun mergedAccounts(origins: Set<String>): AccountSnapshot = mergedAccountCache.get(origins)

    private fun computeAccountMerge(origins: Set<String>) = accountRepositories.asSequence()
        .flatMap { repo ->
            sequenceOf(repo.mergedAccounts(origins))
        }
        .toList()
        .let { AccountSnapshot.merge(it, AccountSnapshot.ALL) }
        ?: error("a merged account cannot be null")

    fun getAccount(origin: String): AccountSnapshot? = accountRepositories.asSequence().flatMap { repo ->
        repo.getAccount(origin)?.let { sequenceOf(it) } ?: emptySequence()
    }.toList().let { AccountSnapshot.merge(it, origin) }

    fun getKnownCurrencies(filterFiat: Boolean = true): Set<Currency> = accountRepositories
        .flatMapTo(mutableSetOf()) { it.getKnownCurrencies(filterFiat) }

    fun insert(account: AccountSnapshot) {
        log.debug("insert {} account data", account.origin)
        accountRepositories.forEach {
            if (it.supportWrite()) {
                it.insert(account)
            }
        }
    }
}