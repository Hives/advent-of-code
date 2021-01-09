package days.day01

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

internal class CombinationTest {
    @Test
    fun `can't choose more than there are items in the list`() {
        assertThrows<IllegalArgumentException> { Combination(listOf('a', 'b', 'c', 'd'), 5) }
    }

    @Test
    fun `first combination is first n items`() {
        val c = Combination(listOf('a', 'b', 'c', 'd', 'e'), 3).asSequence()
        assertThat(c.first()).isEqualTo(listOf('a', 'b', 'c'))
    }

    @Test
    fun `first combinations increment last item`() {
        val c = Combination(listOf('a', 'b', 'c', 'd', 'e'), 3)
        val list = c.asSequence().take(3).toList()
        assertThat(list[1]).isEqualTo(listOf('a', 'b', 'd'))
        assertThat(list[2]).isEqualTo(listOf('a', 'b', 'e'))
    }

    @Test
    fun `when last item is maximally incremented, it increments second last item`() {
        val c = Combination(listOf('a', 'b', 'c', 'd', 'e'), 3)
        val fourth = c.asSequence().take(4).toList().last()
        assertThat(fourth).isEqualTo(listOf('a', 'c', 'd'))
    }
}