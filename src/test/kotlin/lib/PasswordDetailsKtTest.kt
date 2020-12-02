package lib

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PasswordDetailsKtTest {
    @Nested
    inner class NewValidationMethod {
        @Test
        fun `neither position matches`() {
            val details = PasswordDetails("password", 'd', 0, 1)
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `both positions match`() {
            val details = PasswordDetails("password", 's', 2, 3)
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `first position matches`() {
            val details = PasswordDetails("password", 'a', 1, 3)
            assertThat(details.validate()).isTrue()
        }

        @Test
        fun `second position matches`() {
            val details = PasswordDetails("password", 's', 1, 3)
            assertThat(details.validate()).isTrue()
        }
    }

    @Nested
    inner class OldValidationMethod {
        @Test
        fun `too few of character`() {
            val details = OldPasswordDetails("password", 'a', 2, 99)
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `too many of character`() {
            val details = OldPasswordDetails("password", 's', 0, 1)
            assertThat(details.validate()).isFalse()
        }

        @Test
        fun `lower bound`() {
            val details = OldPasswordDetails("password", 's', 2, 3)
            assertThat(details.validate()).isTrue()
        }

        @Test
        fun `upper bound`() {
            val details = OldPasswordDetails("password", 's', 1, 2)
            assertThat(details.validate()).isTrue()
        }
    }
}