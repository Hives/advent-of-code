package days.day08

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess
import lib.Vector3

fun main() {
    val input = Reader("/day08/input.txt").strings()
    val (part1, part2) = Reader("/day08/answers.txt").longs()
    val exampleInput = Reader("/day08/example-1.txt").strings()

    time(warmUp = 10, iterations = 10, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(warmUp = 10, iterations = 10, message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>): Long {
    val junctionBoxes = parseInput(input)
    val pairsSortedByDistances =
        junctionBoxes.getPairs()
            .map { Pair(it, (it.second - it.first).straightLineDistance) }
            .sortedBy { it.second }
    val closest = pairsSortedByDistances.take(1_000)
    val circuits = junctionBoxes.map { mutableSetOf(it) }.toMutableSet()
    closest.forEach { (pair, _) ->
        val (a, b) = pair
        val circuitContainingA = circuits.single { it.contains(a) }
        if (!circuitContainingA.contains(b)) {
            val circuitContainingB = circuits.single { it.contains(b) }
            circuits.removeIf { it.contains(b) }
            circuitContainingA.addAll(circuitContainingB)
        }
    }
    return circuits.map { it.size.toLong() }.sortedDescending().take(3).let { it[0] * it[1] * it[2] }
}

fun part2(input: List<String>): Long {
    val junctionBoxes = parseInput(input)
    val pairsSortedByDistances =
        junctionBoxes.getPairs()
            .map { Pair(it, (it.second - it.first).straightLineDistance) }
            .sortedBy { it.second }
    val circuits = junctionBoxes.map { mutableSetOf(it) }.toMutableSet()
    lateinit var final: Pair<Vector3, Vector3>
    pairsSortedByDistances.forEach { (pair, _) ->
        if (circuits.size == 1 && circuits.single().size == junctionBoxes.size) {
            return@forEach
        } else {
            final = pair
            val (a, b) = pair
            val circuitContainingA = circuits.single { it.contains(a) }
            if (!circuitContainingA.contains(b)) {
                val circuitContainingB = circuits.single { it.contains(b) }
                circuits.removeIf { it.contains(b) }
                circuitContainingA.addAll(circuitContainingB)
            }
        }
    }
    return final.first.x * final.second.x
}

fun <T> List<T>.getPairs(): List<Pair<T, T>> {
    val output = mutableListOf<Pair<T, T>>()
    forEachIndexed { i, first ->
        drop(i + 1).forEach { second ->
            output.add(Pair(first, second))
        }
    }
    return output
}

fun parseInput(input: List<String>): List<Vector3> =
    input.map {
        val (x, y, z) = it.split(",")
        Vector3(x.toInt(), y.toInt(), z.toInt())
    }
