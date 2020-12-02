package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ReaderTest {
    @Test
    fun `can get lines of file as list of strings`() {
        val actual = Reader("list-of-ints.txt").strings()
        assertThat(actual).isEqualTo(listOf("1", "2", "3"))
    }

    @Test
    fun `can read a file which is a list of ints`() {
        val actual = Reader("list-of-ints.txt").ints()
        assertThat(actual).isEqualTo(listOf(1, 2, 3))
    }
}