package days.day19

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class MessageValidatorKtTest {
    private val matches42 = "matches rule 42"
    private val rule42 = """$matches42"""
    private val matches31 = "matches rule 42matches rule 31"
    private val rule31 = """$matches31"""

    @Test
    fun basic() {
        assertThat(rule42.toRegex().matches(matches42)).isTrue()
        assertThat(rule42.toRegex().matches(matches31)).isFalse()
        assertThat(rule31.toRegex().matches(matches42)).isFalse()
        assertThat(rule31.toRegex().matches(matches31)).isTrue()
    }

    @Nested
    inner class Repetition {
        private val string1 = "s"
        private val string2 = "t"

        @Test
        fun `how does repetition work`() {
            val rule1 = """($string1){1}""".toRegex()
            assertThat(rule1.matches("s")).isTrue()
            assertThat(rule1.matches("ss")).isFalse()

            val rule2 = """($string1){2}""".toRegex()
            assertThat(rule2.matches("s")).isFalse()
            assertThat(rule2.matches("ss")).isTrue()
        }

        @Test
        fun `huh?`() {
            val rule1 = """($string1)+($string1){3}($string2){3}""".toRegex()
            assertThat(rule1.matches("ssssttt")).isTrue()
        }
    }

    @Test
    fun `asd asd`() {
        assertThat(testMessageInPart2("$matches42$matches31", rule42, rule31)).isFalse()
        assertThat(testMessageInPart2("$matches42$matches42$matches31", rule42, rule31)).isTrue()
        assertThat(testMessageInPart2("$matches42$matches42$matches42$matches31", rule42, rule31)).isTrue()
        assertThat(testMessageInPart2("$matches42$matches42$matches42$matches31$matches31", rule42, rule31)).isTrue()
        assertThat(testMessageInPart2("$matches42$matches31$matches31", rule42, rule31)).isFalse()
        assertThat(testMessageInPart2("$matches42$matches42$matches31$matches31$matches31", rule42, rule31)).isFalse()
    }
}