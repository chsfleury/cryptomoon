package fr.chsfleury.cryptomoon.domain.service

import fr.chsfleury.cryptomoon.domain.listener.AccountUpdateListener
import fr.chsfleury.cryptomoon.domain.listener.QuoteUpdateListener
import fr.chsfleury.cryptomoon.domain.model.AccountSnapshot
import fr.chsfleury.cryptomoon.domain.model.Currency
import fr.chsfleury.cryptomoon.domain.repository.AccountRepository
import fr.chsfleury.cryptomoon.utils.Logging
import fr.chsfleury.cryptomoon.utils.logger

class AccountService(
    private val accountRepositories: Collection<AccountRepository>
): QuoteUpdateListener, Logging {
    private val log = logger()
    private val accountUpdateListeners = mutableSetOf<AccountUpdateListener>()

    private var allAccountCache: Set<AccountSnapshot>? = null
    private val mergedAccountCache = mutableMapOf<Set<String>, AccountSnapshot>()

    fun allAccounts(): Set<AccountSnapshot> {
        return allAccountCache ?: getAllAccounts().also { allAccountCache = it }
    }

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

    fun mergedAccounts(origins: Set<String>): AccountSnapshot {
        return mergedAccountCache.computeIfAbsent(origins) { o ->
            accountRepositories.asSequence().flatMap { repo ->
                sequenceOf(repo.mergedAccounts(o))
            }.toList().let { AccountSnapshot.merge(it, AccountSnapshot.ALL) } ?: error("a merged account cannot be null")
        }
    }

    fun getAccount(origin: String): AccountSnapshot? = accountRepositories.asSequence().flatMap { repo ->
        repo.getAccount(origin)?.let { sequenceOf(it) } ?: emptySequence()
    }.toList().let { AccountSnapshot.merge(it, origin) }

    fun getKnownCurrencies(): Set<Currency> = accountRepositories.flatMapTo(mutableSetOf(), AccountRepository::getKnownCurrencies)

    fun insert(account: AccountSnapshot) {
        log.debug("insert {} account data", account.origin)
        accountRepositories.forEach {
            if (it.supportWrite()) {
                it.insert(account)
            }
        }
        notifyAccountUpdate(listOf(account))
    }

    fun addListener(accountUpdateListener: AccountUpdateListener) {
        accountUpdateListeners.add(accountUpdateListener)
    }

    private fun notifyAccountUpdate(accounts: Collection<AccountSnapshot>) {
        val obsoleteOrigins = accounts.map(AccountSnapshot::origin)

        log.debug("invalidate all-accounts cache")
        allAccountCache = null

        log.debug("invalidate merged cache for {}", obsoleteOrigins)
        mergedAccountCache.keys
            .filter { key -> obsoleteOrigins.any { origin -> origin in key } }
            .forEach { mergedAccountCache.remove(it) }

        log.debug("notify listeners of {} account update", obsoleteOrigins)
        accountUpdateListeners.forEach {
            it.onAccountUpdate(obsoleteOrigins)
        }
    }

    override fun onQuoteUpdate(currencies: Set<Currency>) {
        val accountSnapshots = allAccounts()
            .filter { account ->
                account.balances.any { b -> b.currency in currencies }
            }

        notifyAccountUpdate(accountSnapshots)
    }
}