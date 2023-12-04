package lib

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IntKtTest {
    @Nested
    inner class Pow {
        @Test
        fun `3 ^ 0 == 1`() {
            assertThat(3.pow(0)).isEqualTo(1)
        }

        @Test
        fun `2 ^ 2 == 4`() {
            assertThat(2.pow(2)).isEqualTo(4)
        }

        @Test
        fun `4 ^ 4 == 256`() {
            assertThat(4.pow(4)).isEqualTo(256)
        }

        @Test
        fun `throws if exponent less than 0`() {
            assertFailure { 4.pow(-1) }
                .hasMessage("exponent must be greater than 0, but was -1")
        }
    }
}
