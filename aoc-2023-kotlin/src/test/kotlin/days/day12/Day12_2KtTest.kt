package days.day12

import assertk.assertThat
import assertk.assertions.isTrue
import assertk.assertions.matches
import days.day12_2.matches
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day12_2KtTest {
    @Nested
    inner class ConditionMatching {
        @Test
        fun `asdasd`() {
            val condition = ".??..??...?#"
            val string    = "...........#"
            assertThat(string.matches(condition)).isTrue()
        }
    }
}
