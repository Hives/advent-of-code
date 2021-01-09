package days.day11

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ChairsPart2KtTest {
    @Test
    fun `seeing chairs`() {
        val example1 = listOf(
            ".......#.",
            "...#.....",
            ".#.......",
            ".........",
            "..#L....#",
            "....#....",
            ".........",
            "#........",
            "...#....."
        )
        assertThat(getVisibleChairs(3, 4, example1)).isEqualTo(listOf('#', '#', '#', '#', '#', '#', '#', '#'))
    }

    @Test
    fun `example 1 individualSteps`() {
        var chairs = example
        expectedSequence.forEachIndexed { index, expected ->
            chairs = newChairs2(chairs)
            assertThat(chairs).isEqualTo(expected)
        }

        val oneMore = newChairs2(chairs)
        assertThat(oneMore).isEqualTo(expectedSequence.last())
    }

    @Test
    fun `example all steps`() {
        assertThat(run2(example)).isEqualTo(
            listOf(
                "#.L#.L#.L#",
                "#LLLLLL.LL",
                "L.L.L..#..",
                "##L#.#L.L#",
                "L.L#.LL.L#",
                "#.LLLL#.LL",
                "..#.L.....",
                "LLL###LLL#",
                "#.LLLLL#.L",
                "#.L#LL#.L#"
            )
        )

    }

    private val example = ("L.LL.LL.LL\n" +
            "LLLLLLL.LL\n" +
            "L.L.L..L..\n" +
            "LLLL.LL.LL\n" +
            "L.LL.LL.LL\n" +
            "L.LLLLL.LL\n" +
            "..L.L.....\n" +
            "LLLLLLLLLL\n" +
            "L.LLLLLL.L\n" +
            "L.LLLLL.LL").lines()

    private val expectedSequence = listOf(
        ("#.##.##.##\n" +
                "#######.##\n" +
                "#.#.#..#..\n" +
                "####.##.##\n" +
                "#.##.##.##\n" +
                "#.#####.##\n" +
                "..#.#.....\n" +
                "##########\n" +
                "#.######.#\n" +
                "#.#####.##").lines(),

        ("#.LL.LL.L#\n" +
                "#LLLLLL.LL\n" +
                "L.L.L..L..\n" +
                "LLLL.LL.LL\n" +
                "L.LL.LL.LL\n" +
                "L.LLLLL.LL\n" +
                "..L.L.....\n" +
                "LLLLLLLLL#\n" +
                "#.LLLLLL.L\n" +
                "#.LLLLL.L#").lines(),

        ("#.L#.##.L#\n" +
                "#L#####.LL\n" +
                "L.#.#..#..\n" +
                "##L#.##.##\n" +
                "#.##.#L.##\n" +
                "#.#####.#L\n" +
                "..#.#.....\n" +
                "LLL####LL#\n" +
                "#.L#####.L\n" +
                "#.L####.L#").lines(),

        ("#.L#.L#.L#\n" +
                "#LLLLLL.LL\n" +
                "L.L.L..#..\n" +
                "##LL.LL.L#\n" +
                "L.LL.LL.L#\n" +
                "#.LLLLL.LL\n" +
                "..L.L.....\n" +
                "LLLLLLLLL#\n" +
                "#.LLLLL#.L\n" +
                "#.L#LL#.L#").lines(),

        ("#.L#.L#.L#\n" +
                "#LLLLLL.LL\n" +
                "L.L.L..#..\n" +
                "##L#.#L.L#\n" +
                "L.L#.#L.L#\n" +
                "#.L####.LL\n" +
                "..#.#.....\n" +
                "LLL###LLL#\n" +
                "#.LLLLL#.L\n" +
                "#.L#LL#.L#").lines(),

        ("#.L#.L#.L#\n" +
                "#LLLLLL.LL\n" +
                "L.L.L..#..\n" +
                "##L#.#L.L#\n" +
                "L.L#.LL.L#\n" +
                "#.LLLL#.LL\n" +
                "..#.L.....\n" +
                "LLL###LLL#\n" +
                "#.LLLLL#.L\n" +
                "#.L#LL#.L#").lines()
    )
}