package days.day03

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day03.txt").strings()
    val exampleInput = Reader("day03-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 50, iterations = 500) {
        part1(input)
    }.checkAnswer(8515)

    time(message = "Part 2", warmUpIterations = 50, iterations = 500) {
        part2(input)
    }.checkAnswer(2434)
}

fun part1(lines: List<String>): Int =
    lines.sumOf { line ->
        line.toList()
            .let {
                val length = it.size
                it.chunked(length / 2)
            }.let { (first, second) ->
                first.intersect(second.toSet())
            }
            .single()
            .code
            .let {
                if (it >= 97) it - 96
                else it - 38
            }
    }

fun part2(lines: List<String>): Int =
    lines.chunked(3) { group ->
        group
            .map { it.toList() }
            .let { (first, second, third) ->
                first.intersect(second.toSet()).intersect(third.toSet())
            }
            .single()
            .code
            .let {
                if (it >= 97) it - 96
                else it - 38
            }
    }.sum()

