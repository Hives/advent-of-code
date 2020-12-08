package days.day08

import days.day08.Status.FINISHED
import lib.Reader

fun main() {
    val input = Reader("day08.txt").strings()

    val (output1, _) = execute(input)

    println("part 1: ${output1.acc[output1.acc.size - 2]}")

    List(input.size) { input }
        .mapIndexed { index, program ->
            program.replace(index, switchJmpAndNop(program[index]))
        }
        .forEach { program ->
            val (output, status) = execute(program)
            if (status == FINISHED) println("part 2: ${output.acc.last()}")
        }

}
