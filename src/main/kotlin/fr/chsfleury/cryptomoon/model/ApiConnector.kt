package fr.chsfleury.cryptomoon.model

interface ApiConnector {
    fun extract(): BalanceReport
}