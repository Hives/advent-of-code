package days.day03

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day03/input.txt").strings()
    val (part1, part2) = Reader("/day03/answers.txt").longs()
    val exampleInput = Reader("/day03/example-1.txt").strings()

    time(warmUp = 1000, iterations = 1000, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(warmUp = 1000, iterations = 1000, message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>) = input.sumOf { getMax(parseBank(it), 2) }

fun part2(input: List<String>) = input.sumOf { getMax(parseBank(it), 12) }

tailrec fun getMax(bank: List<Int>, batteries: Int, acc: Long = 0): Long {
    return if (batteries == 0) acc
    else {
        val b = bank.dropLast(batteries - 1).max()
        getMax(bank.drop(bank.indexOf(b) + 1), batteries - 1, (10 * acc) + b)
    }
}

fun parseBank(input: String) = input.map { it.digitToInt() }
