package fr.chsfleury.cryptomoon.utils

object MinMax {

    fun <T: Any, R: Comparable<R>> Collection<T>.minMaxOf(valueMapper: (T) -> R): Pair<R, R>? {
        var min: R? = null
        var max: R? = null
        forEach {
            val value = valueMapper(it)
            if (min == null || min!! > value) {
                min = value
            }

            if (max == null || max!! < value) {
                max = value
            }
        }
        return min?.to(max!!)
    }

}