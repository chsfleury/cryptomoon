package fr.chsfleury.cryptomoon.model

interface ApiConnector {
    val name: String
    fun report(): BalanceReport
}