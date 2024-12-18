package days.day17

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import lib.Reader
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Day17KtTest {
    @Nested
    inner class ComputerExamples {
        @Test
        fun `example 1`() {
            val program = listOf(2, 6)
            val computer = Computer(program, 0, 0, 9)
            computer.run()
            assertThat(computer.b).isEqualTo(1)
        }

        @Test
        fun `example 2`() {
            val program = listOf(5, 0, 5, 1, 5, 4)
            val computer = Computer(program, 10, 0, 0)
            computer.run()
            assertThat(computer.output).containsExactly(0, 1, 2)
        }

        @Test
        fun `example 3`() {
            val program = listOf(0, 1, 5, 4, 3, 0)
            val computer = Computer(program, 2024, 0, 0)
            computer.run()
            assertThat(computer.output).containsExactly(4, 2, 5, 6, 7, 7, 7, 7, 3, 1, 0)
        }

        @Test
        fun `example 4`() {
            val program = listOf(1, 7)
            val computer = Computer(program, 0, 29, 0)
            computer.run()
            assertThat(computer.b).isEqualTo(26)
        }

        @Test
        fun `example 5`() {
            val program = listOf(4, 0)
            val computer = Computer(program, 0, 2024, 43690)
            computer.run()
            assertThat(computer.b).isEqualTo(44354)
        }

        @Test
        fun `example 6`() {
            val input = Reader("/day17/example-1.txt").string()
            val computer = Computer.from(input)
            computer.run()
            assertThat(computer.output).containsExactly(4, 6, 3, 5, 6, 3, 5, 2, 1, 0)
        }
    }
}
