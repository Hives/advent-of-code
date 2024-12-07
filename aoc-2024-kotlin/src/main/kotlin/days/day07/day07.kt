package days.day07

import lib.Reader
import lib.checkAnswer
import lib.time
import lib.toLongs

fun main() {
    val input = Reader("/day07/input.txt").strings()
    val exampleInput = Reader("/day07/example-1.txt").strings()

    time(message = "Part 1", iterations = 10, warmUp = 20) {
        part1(input)
    }.checkAnswer(4364915411363)

    time(message = "Part 2", iterations = 10, warmUp = 10) {
        part2(input)
    }.checkAnswer(38322057216320)
}

fun part1(input: List<String>) =
    solve(input, listOf(Long::plus, Long::times))

fun part2(input: List<String>) =
    solve(input, listOf(Long::plus, Long::times, Long::concat))

fun solve(input: List<String>, allowedOperations: List<LongLongToLong>) =
    input.map(::parseInput)
        .filter { it.isResolvable(allowedOperations) }
        .sumOf { it.total }

data class Equation(val total: Long, val numbers: List<Long>) {
    fun isResolvable(
        allowedOperations: List<LongLongToLong>
    ): Boolean {
        fun go(total: Long, acc: Long, numbers: List<Long>): Boolean =
            if (numbers.isEmpty()) acc == total
            else allowedOperations.any { operation ->
                go(
                    total = total,
                    acc = operation(acc, numbers.first()),
                    numbers = numbers.subList(1, numbers.size)
                )
            }

        return go(total, numbers.first(), numbers.subList(1, numbers.size))
    }
}

fun Long.concat(that: Long): Long {
    var powerOfTen = 10;
    while (that >= powerOfTen) powerOfTen *= 10
    return this * powerOfTen + that
}

fun parseInput(input: String): Equation =
    input.split(": ")
        .let { Equation(it[0].toLong(), it[1].toLongs()) }

typealias LongLongToLong = (Long, Long) -> Long
