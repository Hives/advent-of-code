package days.day07

import assertk.assertThat
import assertk.assertions.isGreaterThan
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day07Test {
    @Nested
    inner class Ordering {
        @Test
        fun `ordering`() {
            val hand1 = Hand.parse1("AAAAA 1")
            val hand2 = Hand.parse1("QQQJJ 1")
            assertThat(hand1).isGreaterThan(hand2)
        }
    }
}
