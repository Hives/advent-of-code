package days.day02

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Day 2: validating passwords")
internal class PasswordsKtTest {
    @Nested
    inner class ExtractingPasswordDetails {
        @Test
        fun `can extract a password`() {
            assertThat("1-2 a: password".extractPassword()).isEqualTo("password")
        }

        @Test
        fun `can extract an old password criteria`() {
            assertThat("1-2 a: password".extractOldPolicy())
                .isEqualTo(OldPolicy('a', 1, 2))
        }

        @Test
        fun `can extract a new password criteria`() {
            assertThat("1-2 a: password".extractNewPolicy())
                .isEqualTo(NewPolicy('a', 0, 1))
        }
    }

    @Nested
    inner class NewValidationMethod {
        @Test
        fun `neither position matches`() {
            val criteria = NewPolicy(
                char = 'd',
                pos1 = 0,
                pos2 = 1
            )
            assertThat(criteria.validate("password")).isFalse()
        }

        @Test
        fun `both positions match`() {
            val criteria = NewPolicy(
                char = 's',
                pos1 = 2,
                pos2 = 3
            )
            assertThat(criteria.validate("password")).isFalse()
        }

        @Test
        fun `first position matches`() {
            val criteria = NewPolicy(
                char = 'a',
                pos1 = 1,
                pos2 = 3
            )
            assertThat(criteria.validate("password")).isTrue()
        }

        @Test
        fun `second position matches`() {
            val criteria = NewPolicy(
                char = 's',
                pos1 = 1,
                pos2 = 3
            )
            assertThat(criteria.validate("password")).isTrue()
        }
    }

    @Nested
    inner class OldValidationMethod {
        @Test
        fun `false if character appears too few times`() {
            val criteria = OldPolicy(
                char = 'a',
                min = 2,
                max = 99
            )
            assertThat(criteria.validate("password")).isFalse()
        }

        @Test
        fun `false if character appears too many times`() {
            val criteria = OldPolicy(
                char = 's',
                min = 0,
                max = 1
            )
            assertThat(criteria.validate("password")).isFalse()
        }

        @Test
        fun `true if occurrences = lower bound`() {
            val criteria = OldPolicy(
                char = 's',
                min = 2,
                max = 3
            )
            assertThat(criteria.validate("password")).isTrue()
        }

        @Test
        fun `true if occurrences = upper bound`() {
            val criteria = OldPolicy(
                char = 's',
                min = 1,
                max = 2
            )
            assertThat(criteria.validate("password")).isTrue()
        }
    }
}