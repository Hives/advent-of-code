package days.day01

import kotlin.math.abs
import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day01/input.txt").strings()
    val exampleInput = Reader("/day01/example-1.txt").strings()

    time(message = "Part 1", warmUpIterations = 500) {
        part1(input)
    }.checkAnswer(2375403)

    time(message = "Part 2", warmUpIterations = 500) {
        part2(input)
    }.checkAnswer(23082277)
}

fun part1(input: List<String>): Int {
    val (col1, col2) = parseInput(input)
    return (col1.sorted() zip col2.sorted()).sumOf { abs(it.first - it.second) }
}

fun part2(input: List<String>): Int {
    val (col1, col2) = parseInput(input)
    return col1.fold(0) { acc, n1 ->
        acc + (n1 * col2.count { n2 -> n1 == n2 })
    }
}

fun parseInput(input: List<String>): Pair<List<Int>, List<Int>> =
    input.fold(Pair(emptyList(), emptyList())) { acc, line ->
        val (col1, col2) = line.split("   ").map(String::toInt)
        Pair(acc.first + col1, acc.second + col2)
    }
