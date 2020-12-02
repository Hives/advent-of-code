package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FindNumbersThatAddToKtTest {
    @Nested
    inner class FindTwoNumbersThatAddTo {
        @Test
        fun `example`() {
            val input = listOf(1, 2, 3, 4, 5)
            assertThat(input.findTwoNumbersThatAddTo(9)).isEqualTo(setOf(4, 5))
        }

        @Test
        fun `example from question`() {
            val input = listOf(1721, 979, 366, 299, 675, 1456)
            assertThat(input.findTwoNumbersThatAddTo(2020)).isEqualTo(setOf(1721, 299))
        }
    }

    @Nested
    inner class FindThreeNumbersThatAddTo {
        @Test
        fun `example from question`() {
            val input = listOf(1721, 979, 366, 299, 675, 1456)
            assertThat(input.findThreeNumbersThatAddTo(2020)).isEqualTo(setOf(979, 366, 675))
        }
    }
}
