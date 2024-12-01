package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StringKtTest {
    @Nested
    inner class IndexesOf {
        @Test
        fun `can find indexes of multiple sub strings`() {
            assertThat("fooBARfooBARBAR".indexesOf("BAR")).isEqualTo(listOf(3, 9, 12))
        }

        @Test
        fun `returns empty list if no matches`() {
            assertThat("foofoofoo".indexesOf("BAR")).isEqualTo(emptyList())
        }
    }
}
