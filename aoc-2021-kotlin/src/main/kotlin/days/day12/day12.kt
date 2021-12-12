package days.day12

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day12.txt").strings()
    val exampleInput = Reader("day12-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(3887)

    time(iterations = 5, warmUpIterations = 0, message = "Part 2") {
        part2(input)
    }.checkAnswer(104834)
}

fun part1(input: List<String>): Int = countPaths(input, ::pathValidator1)
fun part2(input: List<String>): Int = countPaths(input, ::pathValidator2)

fun countPaths(
    input: List<String>,
    pathValidator: (List<String>, String) -> Boolean,
): Int {
    val caveConnections = parseInput(input)

    fun go(journeys: List<List<String>>): List<List<String>> {
        val extensions = tick(journeys, caveConnections, pathValidator)

        return if (extensions.size == journeys.size) journeys
        else go(extensions)
    }

    val allJourneys = go(listOf(listOf("start")))

    return allJourneys.size
}

fun tick(
    journeys: List<List<String>>,
    caveConnections: Map<String, List<String>>,
    pathValidator: (List<String>, String) -> Boolean,
): List<List<String>> =
    journeys.flatMap { journey ->
        if (journey.last() == "end") listOf(journey)
        else {
            extendPath(journey, caveConnections, pathValidator)
        }
    }

fun extendPath(
    journey: List<String>,
    caveConnections: Map<String, List<String>>,
    pathValidator: (List<String>, String) -> Boolean,
): List<List<String>> =
    caveConnections[journey.last()]?.mapNotNull { next ->
        if (pathValidator(journey, next)) journey + next
        else null
    } ?: throw Exception("Unknown cave: ${journey.last()}")

fun pathValidator1(journey: List<String>, next: String) =
    when {
        next == "start" -> false
        next.isLowercase() && next in journey -> false
        else -> true
    }

fun pathValidator2(journey: List<String>, next: String) =
    when {
        next == "start" -> false
        next.isLowercase() && next in journey && journey.containsDuplicateLowercase() -> false
        else -> true
    }

fun List<String>.containsDuplicateLowercase() =
    this.filter { it.isLowercase() }
        .groupingBy { it }
        .eachCount()
        .any { (_, value) -> value > 1 }

fun String.isLowercase() = this == this.lowercase()

fun parseInput(input: List<String>) =
    input
        .map { it.split("-") }
        .flatMap { listOf(it[0] to it[1], it[1] to it[0]) }
        .groupBy { it.first }
        .mapValues { (_, value) -> value.map { it.second } }