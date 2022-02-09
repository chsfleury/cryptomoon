package fr.chsfleury.cryptomoon.domain.listener

interface AccountUpdateListener {
    fun onAccountUpdate(accounts: Collection<String>)
}