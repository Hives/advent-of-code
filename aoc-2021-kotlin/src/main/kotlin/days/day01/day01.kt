package days.day01

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day01.txt").ints()

    time(iterations = 10_000, warmUpIterations = 100, message = "Part 1") {
        input.countIncreases()
    }

    time(iterations = 10_000, warmUpIterations = 100, message = "Part 2") {
        input.windowed(3).map { it.sum() }.countIncreases()
    }
}

private fun List<Int>.countIncreases() = this.zipWithNext().count { (prev, next) -> next > prev }