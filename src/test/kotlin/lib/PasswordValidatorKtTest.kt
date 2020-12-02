package lib

import assertk.assertThat
import assertk.assertions.isFalse
import org.junit.jupiter.api.Test

internal class PasswordValidatorKtTest {
    @Test
    fun `too few of character`() {
        val details = PasswordDetails("password", 'a', 2, 99)
        assertThat(validatePassword(details)).isFalse()
    }

    @Test
    fun `too many of character`() {
        val details = PasswordDetails("password", 's', 1, 1)
        assertThat(validatePassword(details)).isFalse()
    }
}