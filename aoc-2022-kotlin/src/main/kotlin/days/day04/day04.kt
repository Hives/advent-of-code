package days.day04

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day04.txt").strings()
    val exampleInput = Reader("day04-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 50, iterations = 1000) {
        part1(input)
    }.checkAnswer(453)

    time(message = "Part 2", warmUpIterations = 50, iterations = 1000) {
        part2(input)
    }.checkAnswer(919)
}

fun part1(input: List<String>): Int =
    input.count { it.parse().let { (first, second) -> first.covers(second) || second.covers(first) } }

fun part2(input: List<String>): Int =
    input.count { it.parse().let { (first, second) -> first.overlaps(second) } }

fun String.parse() = split(",").map { it.split("-").map(String::toInt) }

fun List<Int>.covers(that: List<Int>) = this[0] <= that[0] && this[1] >= that[1]

fun List<Int>.overlaps(that: List<Int>) =
    this[0].between(that) || this[1].between(that) || this.covers(that) || that.covers(this)

fun Int.between(that: List<Int>) = this >= that[0] && this <= that[1]
