package days.day23

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day23/input.txt").strings()
    val exampleInput = Reader("/day23/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1200)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<String>): Int {
    val connectionMap = parseInput(input)
    val computers = connectionMap.keys
    return computers.asSequence()
        .filter { it.startsWith("t") }
        .flatMap { connectionMap.findNetworksOfThree(it) }
        .distinct()
        .count()
}

fun part2(input: List<String>): Int {
    return -1
}

fun Map<String, List<String>>.findNetworksOfThree(computer: String): List<List<String>> {
    val connected = this[computer]!!
    val twos = connected.map { listOf(it, computer) }
    return twos.flatMap { (c1, c2) ->
        val extras = this[c1]!!.intersect(this[c2]!!)
        extras.map { c3 ->
            listOf(c1, c2, c3).sorted()
        }
    }.distinct()
}

fun parseInput(input: List<String>): Map<String, List<String>> {
    val pairs = input.map { it.split("-") }
    val computers = pairs.flatten().distinct()
    return computers.associateWith { c1 ->
        pairs.filter { it.contains(c1) }.map { it.single { it != c1 } }
    }
}
