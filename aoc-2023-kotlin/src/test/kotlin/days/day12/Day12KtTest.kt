package days.day12

import assertk.assertThat
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day12KtTest {
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
