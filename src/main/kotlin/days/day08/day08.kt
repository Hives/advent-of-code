package days.day08

import days.day08.FinalStatus.FINITO
import lib.Reader

fun main() {
    val input = Reader("day08.txt").strings()

    execute(input).also { (state, _) -> println("part 1: ${state.accumulator}") }

    List(input.size) { input }
        .mapIndexed { index, program ->
            program.replace(index, switchJmpAndNop(program[index]))
        }
        .forEach { program ->
            val (state, status) = execute(program)
            if (status == FINITO) println("part 2: ${state.accumulator}")
        }

}
