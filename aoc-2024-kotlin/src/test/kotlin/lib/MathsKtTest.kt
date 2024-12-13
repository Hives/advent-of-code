package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Maths {
    @Nested
    inner class GreatestCommonDivisor {
        @Test
        fun `example 1`() {
            assertThat(gcd(30, 650)).isEqualTo(10L)
            assertThat(gcd(650, 30)).isEqualTo(10L)
        }

        @Test
        fun `example 2`() {
            assertThat(gcd(2420, 230)).isEqualTo(10L)
            assertThat(gcd(230, 2420)).isEqualTo(10L)
        }

        @Test
        fun `example 3`() {
            assertThat(gcd(3915, 825)).isEqualTo(15L)
            assertThat(gcd(825, 3915)).isEqualTo(15L)
        }

        @Test
        fun `example 4`() {
            assertThat(gcd(10, 0)).isEqualTo(10L)
            assertThat(gcd(0, 10)).isEqualTo(10L)
        }
    }

    @Nested
    inner class LowestCommonMultiple {
        @Test
        fun `example 1`() {
            assertThat(lcm(24, 35)).isEqualTo(840L)
        }

        @Test
        fun `example 2`() {
            assertThat(lcm(2L, 3L, 4L, 5L)).isEqualTo(60L)
        }
    }
}
