package fr.chsfleury.cryptomoon.application.io.formatter.standard

data class KeyedValue<T: Any>(
    val key: T,
    val value: Any?
)
