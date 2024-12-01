package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LongRangeKtTest {
    @Nested
    inner class Overlap {
        @Test
        fun `no overlap - first is lower`() {
            val range1 = 0L..2L
            val range2 = 10L..12L
            assertThat(range1.overlap(range2)).isEqualTo(null)
        }

        @Test
        fun `no overlap - first is higher`() {
            val range1 = 10L..12L
            val range2 = 0L..2L
            assertThat(range1.overlap(range2)).isEqualTo(null)
        }

        @Test
        fun `this totally overlaps other`() {
            val range1 = 0L..10L
            val range2 = 2L..8L
            assertThat(range1.overlap(range2)).isEqualTo(2L..8L)
        }

        @Test
        fun `other totally overlaps this`() {
            val range1 = 2L..8L
            val range2 = 0L..10L
            assertThat(range1.overlap(range2)).isEqualTo(2L..8L)
        }

        @Test
        fun `this overlaps bottom of other`() {
            val range1 = 0L..5L
            val range2 = 2L..8L
            assertThat(range1.overlap(range2)).isEqualTo(2L..5L)
        }

        @Test
        fun `this overlaps top of other`() {
            val range1 = 2L..8L
            val range2 = 0L..5L
            assertThat(range1.overlap(range2)).isEqualTo(2L..5L)
        }

        @Test
        fun `??`() {
            val range1 = 79L..93L
            val range2 = 98L..99L
            assertThat(range1.overlap(range2)).isEqualTo(null)
        }
    }

    @Nested
    inner class Subtract {
        @Test
        fun `no overlap - this is lower`() {
            val range1 = 0L..2L
            val range2 = 10L..12L
            assertThat(range1.subtract(range2)).isEqualTo(listOf(range1))
        }

        @Test
        fun `no overlap - this is higher`() {
            val range1 = 10L..12L
            val range2 = 0L..2L
            assertThat(range1.subtract(range2)).isEqualTo(listOf(range1))
        }

        @Test
        fun `this totally overlaps other`() {
            val range1 = 0L..10L
            val range2 = 2L..8L
            assertThat(range1.subtract(range2)).isEqualTo(listOf(0L..1L, 9L..10L))
        }

        @Test
        fun `other totally overlaps this`() {
            val range1 = 2L..8L
            val range2 = 0L..10L
            assertThat(range1.subtract(range2)).isEqualTo(emptyList())
        }

        @Test
        fun `this overlaps bottom of other`() {
            val range1 = 0L..5L
            val range2 = 2L..8L
            assertThat(range1.subtract(range2)).isEqualTo(listOf(0L..1L))
        }

        @Test
        fun `this overlaps top of other`() {
            val range1 = 2L..8L
            val range2 = 0L..5L
            assertThat(range1.subtract(range2)).isEqualTo(listOf(6L..8L))
        }
    }

    @Nested
    inner class Consolidate {
        @Test
        fun `single range returns itself`() {
            val range = 1L..10L
            assertThat(listOf(range).consolidate()).isEqualTo(listOf(range))
        }

        @Test
        fun `overlapping ranges returns one big range`() {
            val range1 = 1L..7L
            val range2 = 2L..10L
            assertThat(listOf(range1, range2).consolidate()).isEqualTo(listOf(1L..10L))
        }

        @Test
        fun `complicated example`() {
            val range1 = 1L..7L
            val range2 = 2L..10L
            val range3 = 15L..20L
            val range4 = 19L..20L
            assertThat(listOf(range1, range2, range3, range4).consolidate()).isEqualTo(listOf(1L..10L, 15L..20L))
        }
    }
}
