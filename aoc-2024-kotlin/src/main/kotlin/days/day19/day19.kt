package days.day19

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day19/input.txt").strings()
    val exampleInput = Reader("/day19/example-1.txt").strings()

    part1(exampleInput)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<String>): Int {
    return -1
}

fun part2(input: List<String>): Int {
    return -1
}
