package days.day04

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day04KtTest {
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
    }
}
