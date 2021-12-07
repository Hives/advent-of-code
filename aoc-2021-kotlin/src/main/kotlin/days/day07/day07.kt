package days.day07

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.absoluteValue

fun main() {
    val input = Reader("day07.txt").string()
    val exampleInput = Reader("day07-example.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(356992)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(101268110)
}

fun part1(input: String): Int? =
    solve(parseInput(input), ::getSimpleFuelConsumption)

fun part2(input: String): Int? =
    solve(parseInput(input), ::getIncreasingFuelConsumption)

inline fun solve(positions: List<Int>, calculateFuel: (Int, Int) -> Int): Int? =
    (0..positions.maxOrNull()!!)
        .map { destination ->
            positions.sumOf { position -> calculateFuel(position, destination) }
        }.minOrNull()

fun getSimpleFuelConsumption(start: Int, destination: Int): Int =
    (start - destination).absoluteValue

fun getIncreasingFuelConsumption(start: Int, destination: Int): Int {
    val n = (start - destination).absoluteValue
    return n * (n + 1) / 2
}

fun parseInput(input: String) = input.split(",").map { it.toInt() }
