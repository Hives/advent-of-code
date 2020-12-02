package lib

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PasswordAndCriteriaKtTest {
    @Nested
    inner class NewValidationMethod {
        @Test
        fun `neither position matches`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = NewCriteria(
                    char = 'd',
                    pos1 = 0,
                    pos2 = 1
                )
            )
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `both positions match`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = NewCriteria(
                    char = 's',
                    pos1 = 2,
                    pos2 = 3
                )
            )
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `first position matches`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = NewCriteria(
                    char = 'a',
                    pos1 = 1,
                    pos2 = 3
                )
            )
            assertThat(details.validate()).isTrue()
        }

        @Test
        fun `second position matches`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = NewCriteria(
                    char = 's',
                    pos1 = 1,
                    pos2 = 3
                )
            )
            assertThat(details.validate()).isTrue()
        }
    }

    @Nested
    inner class OldValidationMethod {
        @Test
        fun `false if character appears too few times`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = OldCriteria(
                    char = 'a',
                    min = 2,
                    max = 99
                )
            )
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `false if character appears too many times`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = OldCriteria(
                    char = 's',
                    min = 0,
                    max = 1
                )
            )
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `true if occurrences = lower bound`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = OldCriteria(
                    char = 's',
                    min = 2,
                    max = 3
                )
            )
            assertThat(details.validate()).isTrue()
        }

        @Test
        fun `true if occurrences = upper bound`() {
            val details = PasswordAndCriteria(
                password = "password",
                criteria = OldCriteria(
                    char = 's',
                    min = 1,
                    max = 2
                )
            )
            assertThat(details.validate()).isTrue()
        }
    }
}