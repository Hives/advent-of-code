package days.day12

import assertk.assertThat
import assertk.assertions.isEqualTo
import days.day12.Instruction1.*
import org.junit.jupiter.api.Test

internal class Part1Test {
    private val exampleInput = "F10\n" +
            "N3\n" +
            "F7\n" +
            "R90\n" +
            "F11"

    @Test
    fun `parse input`() {
        assertThat(parseInput1(exampleInput)).isEqualTo(
            listOf(
                Forward(10),
                North(3),
                Forward(7),
                Right(90),
                Forward(11)
            )
        )
    }

    @Test
    fun `steps`() {
        assertThat(parseInput1(exampleInput).doIt(State1(0, 0, 90))).isEqualTo(
            State1(17, -8, 180),
        )
    }
}