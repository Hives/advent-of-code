package days.day09

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day09/input.txt").listOfListOfLongs()
    val exampleInput = Reader("/day09/example-1.txt").listOfListOfLongs()

    time(message = "Part 1", warmUpIterations = 100) {
        part1(input)
    }.checkAnswer(1861775706)

    time(message = "Part 2", warmUpIterations = 100) {
        part2(input)
    }.checkAnswer(1082)
}

fun part1(input: List<List<Long>>) =
    input.sumOf { getNextValue(it) }

fun part2(input: List<List<Long>>) =
    input.sumOf { getNextValue(it.reversed()) }

fun getNextValue(history: List<Long>): Long =
    if (history.all { it == 0L }) 0L
    else {
        val nextHistory = history.windowed(2).map { (first, second) -> second - first }
        history.last() + getNextValue(nextHistory)
    }
