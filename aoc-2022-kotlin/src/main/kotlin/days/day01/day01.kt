package days.day01

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.max

fun main() {
    val input = Reader("day01.txt").strings()
    val exampleInput = Reader("day01-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(67450)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(199357)
}

fun part1(lines: List<String>): Int {
    var max = 0
    var runningTotal = 0
    for (line in lines) {
        if (line.isNullOrEmpty()) {
            if (runningTotal > max) max = runningTotal
            runningTotal = 0
        } else {
            runningTotal += line.toInt()
        }
    }
    return max(runningTotal, max)
}

fun part2(lines: List<String>): Int {
    val elves = mutableListOf<Int>()
    var runningTotal = 0
    for (line in lines) {
        if (line.isNullOrEmpty()) {
            elves.add(runningTotal)
            runningTotal = 0
        } else {
            runningTotal += line.toInt()
        }
    }
    elves.add(runningTotal)
    elves.sortDescending()
    return elves.take(3).sum()
}
