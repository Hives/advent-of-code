package days.day12

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class Part2KtTest {
    private val exampleInput = "F10\n" +
            "N3\n" +
            "F7\n" +
            "R90\n" +
            "F11"

    @Test
    fun `parse input`() {
        assertThat(parseInput2(exampleInput)).isEqualTo(
            listOf(
                Instruction2.Forward(10),
                Instruction2.North(3),
                Instruction2.Forward(7),
                Instruction2.Right(90),
                Instruction2.Forward(11)
            )
        )
    }

    @Test
    fun `steps`() {
        val initial = State2(0, 0, 10, 1)
        parseInput2(exampleInput).fold(initial) { acc, instruction2 ->
            println(acc)
            println(instruction2)
            instruction2.move(acc)
        }
        val final = parseInput2(exampleInput).doIt(initial)
        assertThat(final.east).isEqualTo(214)
        assertThat(final.north).isEqualTo(-72)
    }

}