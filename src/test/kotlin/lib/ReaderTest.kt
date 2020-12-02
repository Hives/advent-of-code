package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ReaderTest {
    @Test
    fun `can read a file which is a list of ints`() {
        val actual = Reader("list-of-ints.txt").ints()
        assertThat(actual).isEqualTo(listOf(1, 2, 3))
    }

    @Nested
    inner class Passwords {
        @Test
        fun `for new-style passwords, decrements position by 1 to be zero-indexed`() {
            val actual = Reader("list-of-passwords.txt").passwordsAndNewCriteria()
            assertThat(actual).isEqualTo(listOf(
                PasswordAndCriteria("password", NewCriteria('z', 0, 1)),
                PasswordAndCriteria("anotherOne", NewCriteria('a', 2, 3)),
            ))
        }

        @Test
        fun `for old-style passwords, leaves min + max values as they are`() {
            val actual = Reader("list-of-passwords.txt").passwordsAndOldCriteria()
            assertThat(actual).isEqualTo(listOf(
                PasswordAndCriteria("password", OldCriteria('z', 1, 2)),
                PasswordAndCriteria("anotherOne", OldCriteria('a', 3, 4))
            ))
        }
    }
}