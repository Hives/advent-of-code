package days.day19

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day19KtTest {
    @Nested
    inner class GetTowelsMatchingStart {
        private val towelMap = mapOf<Any, Any>(
            'r' to mapOf(
                'X' to emptyMap<Any, Any>(),
                'b' to mapOf(
                    'X' to emptyMap<Any, Any>()
                )
            ),
            'w' to mapOf(
                'r' to mapOf(
                    'X' to emptyMap<Any, Any>()
                )
            ),
            'b' to mapOf(
                'X' to emptyMap<Any, Any>(),
                'w' to mapOf(
                    'u' to mapOf(
                        'X' to emptyMap<Any, Any>()
                    )
                ),
                'r' to mapOf(
                    'X' to emptyMap<Any, Any>()
                )
            ),
            'g' to mapOf(
                'X' to emptyMap<Any, Any>(),
            ),
            'r' to mapOf(
                'X' to emptyMap<Any, Any>(),
                'g' to mapOf(
                    'X' to emptyMap<Any, Any>()
                )
            )
        )

        @Test
        fun `1`() {
            assertThat(getTowelsMatchingStart("gwb", towelMap, maxTowelLength = 3))
                .containsExactly("g")
        }

        @Test
        fun `2`() {
            assertThat(getTowelsMatchingStart("wb", towelMap, maxTowelLength = 3))
                .isEmpty()
        }
    }
}
