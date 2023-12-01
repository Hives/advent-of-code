package days.day01

import lib.Reader
import lib.checkAnswer
import lib.indexesOf
import lib.time

fun main() {
    val input = Reader("/day01/input.txt").strings()
    val exampleInput = Reader("/day01/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(54388)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(53515)
}

fun part1(input: List<String>) =
    input.sumOf {
        val digits = it.replace(Regex("[a-z]"), "")
        10 * digits.first().digitToInt() + digits.last().digitToInt()
    }

fun part2(input: List<String>) = input
    .sumOf { line ->
        Digit.values().fold(emptyList<Pair<Int, Digit>>()) { acc, digit ->
            val digitPositions =
                listOf(digit.text, "${digit.number}")
                    .flatMap { needle -> line.indexesOf(needle).map { Pair(it, digit) } }

            acc + digitPositions
        }
            .sortedBy { it.first }
            .let { 10 * it.first().second.number + it.last().second.number }
    }

enum class Digit(val text: String, val number: Int) {
    ONE("one", 1),
    TWO("two", 2),
    THREE("three", 3),
    FOUR("four", 4),
    FIVE("five", 5),
    SIX("six", 6),
    SEVEN("seven", 7),
    EIGHT("eight", 8),
    NINE("nine", 9),
}
