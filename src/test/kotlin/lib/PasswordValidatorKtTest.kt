package lib

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PasswordValidatorKtTest {
    @Nested
    inner class NewValidationMethod {
        @Test
        fun `neither position matches`() {
            val details = PasswordDetails("password", 'd', 1, 2)
            assertThat(validatePassword(details)).isFalse()
        }

        @Test
        fun `both positions match`() {
            val details = PasswordDetails("password", 's', 3, 4)
            assertThat(validatePassword(details)).isFalse()
        }

        @Test
        fun `first position matches`() {
            val details = PasswordDetails("password", 'a', 2, 4)
            assertThat(validatePassword(details)).isTrue()
        }

        @Test
        fun `second position matches`() {
            val details = PasswordDetails("password", 's', 2, 4)
            assertThat(validatePassword(details)).isTrue()
        }
    }

    @Nested
    inner class OldValidationMethod {
        @Test
        fun `too few of character`() {
            val details = PasswordDetails("password", 'a', 2, 99)
            assertThat(oldValidatePassword(details)).isFalse()
        }

        @Test
        fun `too many of character`() {
            val details = PasswordDetails("password", 's', 1, 1)
            assertThat(oldValidatePassword(details)).isFalse()
        }
    }
}