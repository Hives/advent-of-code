package lib

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test

internal class CombinationsKtTest {
    @Test
    fun `n = 1`() {
        val actual = listOf(1, 2, 3).combinations(1)
        assertThat(actual).containsOnly(listOf(1), listOf(2), listOf(3))
    }

    @Test
    fun `n = 2`() {
        val actual = listOf(1, 2, 3).combinations(2)
        assertThat(actual).containsOnly(listOf(1, 2), listOf(1, 3), listOf(2, 3))
    }

    @Test
    fun `n = 3`() {
        val actual = listOf(1, 2, 3, 4).combinations(3)
        assertThat(actual).containsOnly(listOf(1, 2, 3), listOf(1, 2, 4), listOf(1, 3, 4), listOf(2, 3, 4))
    }

    @Test
    fun `list with duplicates`() {
        val actual = listOf(1, 2, 2).combinations(2)
        assertThat(actual).containsOnly(listOf(1, 2), listOf(1, 2), listOf(2, 2))
    }

    @Test
    fun `list too short`() {
        val actual = listOf(1, 2, 3).combinations(4)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `can do it with non-ints too`() {
        val actual = listOf("one", "two", "three").combinations(2)
        assertThat(actual).containsOnly(
            listOf("one", "two"),
            listOf("one", "three"),
            listOf("two", "three")
        )
    }
}