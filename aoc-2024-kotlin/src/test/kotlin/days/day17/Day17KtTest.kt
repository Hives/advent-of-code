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
            val program = listOf(2L, 6L)
            val computer = Computer(program, 0, 0, 9)
            computer.run()
            assertThat(computer.b).isEqualTo(1L)
        }

        @Test
        fun `example 2`() {
            val program = listOf(5L, 0L, 5L, 1L, 5L, 4L)
            val computer = Computer(program, 10, 0, 0)
            computer.run()
            assertThat(computer.output).containsExactly(0L, 1L, 2L)
        }

        @Test
        fun `example 3`() {
            val program = listOf(0L, 1L, 5L, 4L, 3L, 0L)
            val computer = Computer(program, 2024, 0, 0)
            computer.run()
            assertThat(computer.output).containsExactly(4L, 2L, 5L, 6L, 7L, 7L, 7L, 7L, 3L, 1L, 0L)
        }

        @Test
        fun `example 4`() {
            val program = listOf(1L, 7L)
            val computer = Computer(program, 0, 29, 0)
            computer.run()
            assertThat(computer.b).isEqualTo(26L)
        }

        @Test
        fun `example 5`() {
            val program = listOf(4L, 0L)
            val computer = Computer(program, 0, 2024, 43690)
            computer.run()
            assertThat(computer.b).isEqualTo(44354L)
        }

        @Test
        fun `example 6`() {
            val input = Reader("/day17/example-1.txt").string()
            val computer = Computer.from(input)
            computer.run()
            assertThat(computer.output).containsExactly(4L, 6L, 3L, 5L, 6L, 3L, 5L, 2L, 1L, 0L)
        }
    }
}
