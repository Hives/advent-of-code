package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ReaderTest {
    @Test
    fun `can read a file which is a list of ints`() {
        val actual = Reader("list-of-ints.txt").listOfInts()
        assertThat(actual).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun `can read a file which is a list of passwords and policies (day 2)`() {
        val actual = Reader("list-of-passwords.txt").listOfPasswords()
        assertThat(actual).isEqualTo(listOf(
            PasswordDetails("password", 'z', 1, 2),
            PasswordDetails("anotherone", 'a', 3, 4),
        ))
    }
}