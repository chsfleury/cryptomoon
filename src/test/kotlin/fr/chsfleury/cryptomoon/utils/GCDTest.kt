package fr.chsfleury.cryptomoon.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GCDTest {

    @Test
    fun compute() {
        assertEquals(GCD.compute(52, 24), 4)
        assertEquals(GCD.compute(listOf(72, 60, 9)), 3)
        assertThrows<IllegalStateException> { GCD.compute(listOf(1)) }
    }
}