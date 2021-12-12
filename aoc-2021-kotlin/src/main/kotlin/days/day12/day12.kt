package days.day12

import lib.Reader

fun main() {
    val input = Reader("day12.txt").strings()
    val exampleInput1 = Reader("day12-example1.txt").strings()

    part1(input).also { println(it) }
    part2(input).also { println(it) }
}

fun part1(input: List<String>): Int {
    val caveConnections = parseInput(input)

    fun go(journeys: List<List<String>>): List<List<String>> {
        val extensions = tick(journeys, caveConnections)

        return if (extensions.size == journeys.size) journeys
        else go(extensions)
    }

    val allJourneys = go(listOf(listOf("start")))
    return allJourneys.size
}

fun part2(input: List<String>): Int {
    val caveConnections = parseInput(input)

    fun go(journeys: List<List<String>>): List<List<String>> {
        val extensions = tick2(journeys, caveConnections)

        return if (extensions.size == journeys.size) journeys
        else go(extensions)
    }

    val allJourneys = go(listOf(listOf("start")))

    return allJourneys.size
}

fun tick(
    journeys: List<List<String>>,
    caveConnections: Map<String, List<String>>,
): List<List<String>> = journeys.flatMap { journey ->
    if (journey.last() == "end") listOf(journey)
    else {
        extendPath(journey, caveConnections)
    }
}

fun tick2(
    journeys: List<List<String>>,
    caveConnections: Map<String, List<String>>,
): List<List<String>> = journeys.flatMap { journey ->
    if (journey.last() == "end") listOf(journey)
    else {
        extendPath2(journey, caveConnections)
    }
}

fun extendPath(
    journey: List<String>,
    caveConnections: Map<String, List<String>>,
): List<List<String>> =
    caveConnections[journey.last()]?.mapNotNull { next ->
        if (next.isLowercase() && next in journey) null
        else journey + next
    } ?: throw Exception("Unknown cave: ${journey.last()}")

fun extendPath2(
    journey: List<String>,
    caveConnections: Map<String, List<String>>,
): List<List<String>> =
    caveConnections[journey.last()]?.mapNotNull { next ->
        if (
            next == "start" ||
            next.isLowercase() && next in journey && journey.containsDuplicateLowercase()
        ) null
        else journey + next
    } ?: throw Exception("Unknown cave: ${journey.last()}")

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