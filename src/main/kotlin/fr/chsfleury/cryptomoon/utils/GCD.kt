package fr.chsfleury.cryptomoon.utils

object GCD {

    fun compute(integers: Collection<Int>): Int {
        if (integers.size < 2) {
            error("cannot compute gcd of less of two values")
        }
        val ite = integers.iterator()
        var gcd = compute(ite.next(), ite.next())
        while (ite.hasNext()) {
            gcd = compute(ite.next(), gcd)
        }
        return gcd
    }

    fun compute(a0: Int, b0: Int): Int {
        var a = a0
        var b = b0
        while (a != b) {
            if (a > b) {
                a -= b
            } else {
                b -= a
            }
        }
        return a
    }

}