package days.day08

import assertk.assertThat
import assertk.assertions.isEqualTo
import days.day08.FinalStatus.FINITO
import days.day08.FinalStatus.INFINITE_LOOP
import org.junit.jupiter.api.Test

internal class ComputerKtTest {
    @Test
    fun nop() {
        val program = listOf("nop +99")
        val output = computeNextState(State.INITIAL, program)
        assertThat(output).isEqualTo(State(1, 0))
    }

    @Test
    fun `jmp positive`() {
        val program = listOf("jmp +27")
        val output = computeNextState(State.INITIAL, program)
        assertThat(output).isEqualTo(State(27, 0))
    }

    @Test
    fun `jmp negative`() {
        val program = listOf("jmp -27")
        val output = computeNextState(State.INITIAL, program)
        assertThat(output).isEqualTo(State(-27, 0))
    }

    @Test
    fun `acc positive`() {
        val program = listOf("acc +27")
        val output = computeNextState(State.INITIAL, program)
        assertThat(output).isEqualTo(State(1, 27))
    }

    @Test
    fun `acc negative`() {
        val program = listOf("acc -27")
        val output = computeNextState(State.INITIAL, program)
        assertThat(output).isEqualTo(State(1, -27))
    }

    @Test
    fun `looping example`() {
        val program = ("nop +0\n" +
                "acc +1\n" +
                "jmp +4\n" +
                "acc +3\n" +
                "jmp -3\n" +
                "acc -99\n" +
                "acc +1\n" +
                "jmp -4\n" +
                "acc +6\n").trim().lines()
        val (state, status) = execute(program)
        assertThat(state.accumulator).isEqualTo(5)
        assertThat(status).isEqualTo(INFINITE_LOOP)
    }

    @Test
    fun `terminating example`() {
        val program = ("nop +0\n" +
                "acc +1\n" +
                "jmp +4\n" +
                "acc +3\n" +
                "jmp -3\n" +
                "acc -99\n" +
                "acc +1\n" +
                "nop -4\n" +
                "acc +6\n").trim().lines()
        val (state, status) = execute(program)
        assertThat(state.accumulator).isEqualTo(8)
        assertThat(status).isEqualTo(FINITO)
    }

    @Test
    fun `list replace`() {
        val input = listOf('a', 'b', 'c')
        assertThat(input.replace(0, 'z')).isEqualTo(listOf('z', 'b', 'c'))
        assertThat(input.replace(1, 'z')).isEqualTo(listOf('a', 'z', 'c'))
        assertThat(input.replace(2, 'z')).isEqualTo(listOf('a', 'b', 'z'))
    }

    @Test
    fun `switch jmp and nop`() {
        assertThat(switchJmpAndNop("jmp +1")).isEqualTo("nop +1")
        assertThat(switchJmpAndNop("nop +1")).isEqualTo("jmp +1")
        assertThat(switchJmpAndNop("acc +1")).isEqualTo("acc +1")
    }
}