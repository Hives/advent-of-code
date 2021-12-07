package days.day07

import lib.Reader
import kotlin.math.absoluteValue

fun main() {
    val input = Reader("day07.txt").string()
    val exampleInput = Reader("day07-example.txt").string()

    part1(input)
    part2(input)
}

fun part1(input: String) {
    val crabs = parseInput(input)

    (crabs.minOrNull()!!..crabs.maxOrNull()!!).associateWith { origin ->
        crabs.sumOf { (it - origin).absoluteValue }
    }.minByOrNull { (_, fuel) -> fuel }
        .also { println(it) }
}

fun part2(input: String) {
    val crabs = parseInput(input)

    (crabs.minOrNull()!!..crabs.maxOrNull()!!).associateWith { origin ->
        crabs.sumOf {
            val n = (origin - it).absoluteValue
            n * (n + 1) / 2
        }
    }.minByOrNull { (_, fuel) -> fuel }
        .also { println(it) }
}

fun parseInput(input: String) = input.split(",").map { it.toInt() }
