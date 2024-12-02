package days.day02

import kotlin.math.abs
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day02/input.txt").listOfListOfInts()
    val exampleInput = Reader("/day02/example-1.txt").listOfListOfInts()

    time(message = "Part 1", warmUp = 100) {
        part1(input)
    }.checkAnswer(472)

    time(message = "Part 2", warmUp = 100) {
        part2(input)
    }.checkAnswer(520)
}

fun part1(input: List<List<Int>>) =
    input.count { it.isSafe() }

fun part2(input: List<List<Int>>): Int =
    input.count { levels ->
        val problemDampenedLevels = levels.indices.map { idx -> levels.dropIndex(idx) }
        levels.isSafe() || problemDampenedLevels.any { it.isSafe() }
    }

fun List<Int>.dropIndex(i: Int) =
    take(i) + drop(i + 1)

fun List<Int>.isSafe() =
    (allIncreasing() || allDecreasing()) && adjacentValuesAreClose()

fun List<Int>.allIncreasing() =
    windowed(2, 1).all { (n1, n2) ->
        n1 < n2
    }

fun List<Int>.allDecreasing() =
    windowed(2, 1).all { (n1, n2) ->
        n1 > n2
    }

fun List<Int>.adjacentValuesAreClose() =
    windowed(2, 1).all { (n1, n2) ->
        abs(n1 - n2) in 1..3
    }
