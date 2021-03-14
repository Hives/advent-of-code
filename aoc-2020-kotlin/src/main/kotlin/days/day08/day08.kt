package days.day08

import days.day08.FinalStatus.FINITO
import lib.Reader
import lib.time

fun main() {
    val input = Reader("day08.txt").strings()

    execute(input).also { (state, _) -> println("part 1: ${state.accumulator}") }

    fun part2BruteForce(): Int {
        val programs = List(input.size) { input }
            .mapIndexed { index, program ->
                program.replace(index, switchJmpAndNop(program[index]))
            }

        programs.forEach { program ->
            val (state, status) = execute(program)
            if (status == FINITO) return state.accumulator
        }

        throw Exception("No answer found")
    }

    fun part2WithASequence(): Int {
        var i = 1
        generateSequence {
            while (input[i].take(3) == "acc") {
                i++
            }
            input.replace(i, switchJmpAndNop(input[i]))
                .also { i++ }
        }
            .map { program -> execute(program) }
            .find { (_, status) ->
                status == FINITO
            }
            ?.also { (state, _) ->
                return state.accumulator
            }

        throw Exception("No answer found")
    }

    time("part 2, brute force", 100) { part2BruteForce() }
    time("part 2, also brute force, but with a sequence", 100) { part2WithASequence() }
}
