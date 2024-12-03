package days.day03

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day03/input.txt").string()
    val exampleInput1 = Reader("/day03/example-1.txt").string()
    val exampleInput2 = Reader("/day03/example-2.txt").string()

    time(message = "Part 1", warmUp = 500) {
        part1(input)
    }.checkAnswer(187825547)

    time(message = "Part 2", warmUp = 500) {
        part2(input)
    }.checkAnswer(85508223)
}

fun part1(input: String) =
    """mul\((\d+),(\d+)\)""".toRegex().findAll(input)
        .fold(0) { acc, matchResult ->
            val (_, n1, n2) = matchResult.groupValues
            acc + (n1.toInt() * n2.toInt())
        }

fun part2(input: String) =
    """mul\((\d+),(\d+)\)|don't\(\)|do\(\)""".toRegex().findAll(input)
        .fold(Pair(0, true)) { (total, enabled), matchResult ->
            val newTotal =
                if (enabled && matchResult.value.startsWith("mul")) {
                    val (_, n1, n2) = matchResult.groupValues
                    total + (n1.toInt() * n2.toInt())
                } else {
                    total
                }
            val newEnabled = when (matchResult.value) {
                "don't()" -> false
                "do()" -> true
                else -> enabled
            }
            Pair(newTotal, newEnabled)
        }.first
