package days.day07

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import lib.Reader
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BagRulesKtTest {
    @Nested
    inner class Parsing {
        @Test
        fun `parse a string`() {
            val actual = listOf("dark orange bags contain 1 bright white bag, 4 muted yellow bags.").parseBagRules()

            assertThat(actual).isEqualTo(
                mapOf(
                    "dark orange" to listOf(
                        "bright white" to 1,
                        "muted yellow" to 4
                    )
                )
            )
        }

        @Test
        fun `parse a longer string`() {
            val actual =
                listOf("dark maroon bags contain 2 striped silver bags, 4 mirrored maroon bags, 5 shiny gold bags, 1 dotted gold bag.")
                    .parseBagRules()

            assertThat(actual).isEqualTo(
                mapOf(
                    "dark maroon" to listOf(
                        "striped silver" to 2,
                        "mirrored maroon" to 4,
                        "shiny gold" to 5,
                        "dotted gold" to 1
                    )
                )
            )
        }

        @Test
        fun `parse a bag that contains no other bags`() {
            val actual = listOf("faded blue bags contain no other bags.").parseBagRules()

            assertThat(actual).isEqualTo(
                mapOf("faded blue" to emptyList())
            )
        }

        @Test
        fun `parse multiple lines`() {
            val actual = listOf(
                "dark olive bags contain 3 faded blue bags, 4 dotted black bags.",
                "vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.",
                "faded blue bags contain no other bags."
            ).parseBagRules()

            assertThat(actual).isEqualTo(
                mapOf(
                    "dark olive" to listOf(
                        "faded blue" to 3,
                        "dotted black" to 4
                    ),
                    "vibrant plum" to listOf(
                        "faded blue" to 5,
                        "dotted black" to 6
                    ),
                    "faded blue" to emptyList()
                )
            )
        }
    }

    @Nested
    inner class GetContainers {
        private val bagMap = Reader("day07-example.txt").strings().parseBagRules()

        @Test
        fun `get immediate containers`() {
            assertThat("shiny gold".getImmediateContainers(bagMap)).containsOnly("bright white", "muted yellow")
        }

        @Test
        fun `get all containers`() {
            assertThat("shiny gold".getAllContainers(bagMap)).containsOnly("bright white", "muted yellow", "dark orange", "light red")
        }

        @Test
        fun `count contents`() {
            assertThat("shiny gold".countContents(bagMap)).isEqualTo(32)
        }
    }
}