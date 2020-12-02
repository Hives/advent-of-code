package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ReaderTest {
    @Test
    fun `can read a file which is a list of ints`() {
        val actual = Reader("list-of-ints.txt").listOfInts()
        assertThat(actual).isEqualTo(listOf(1, 2, 3))
    }

    @Nested
    inner class Passwords {
        @Test
        fun `for new-style passwords, decrements position by 1 to be zero-indexed`() {
            val actual = Reader("list-of-passwords.txt").listOfPasswordDetails()
            assertThat(actual).isEqualTo(listOf(
                PasswordDetails("password", 'z', 0, 1),
                PasswordDetails("anotherone", 'a', 2, 3),
            ))
        }

        @Test
        fun `for old-style passwords, leaves min + max values as they are`() {
            val actual = Reader("list-of-passwords.txt").listOfOldPasswordDetails()
            assertThat(actual).isEqualTo(listOf(
                OldPasswordDetails("password", 'z', 1, 2),
                OldPasswordDetails("anotherone", 'a', 3, 4),
            ))
        }
    }
}