package days.day10

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day10/input.txt").strings()
    val (part1, part2) = Reader("/day10/answers.txt").ints()
    val exampleInput = Reader("/day10/example-1.txt").strings()

    time(warmUp = 10, iterations = 10, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>): Int =
    input.map(::parse).mapNotNull {
        val smallest = findSmallestNumberOfPresses(it)
        if (smallest == null) {
            println("oh no $it")
        }
        smallest
    }.sum()

fun part2(input: List<String>): Long {
    TODO()
}

fun findSmallestNumberOfPresses(config: ButtonConfig): Int? {
    val (_, buttons) = config
    (1..10).forEach { n ->
        val permutationsOfNPresses = getPressPermutations(buttons.size, n)
        if (
            permutationsOfNPresses.any { testPressPermutation(it, config) }
        ) return n
    }
    return null
}

fun testPressPermutation(combo: List<Int>, config: ButtonConfig): Boolean {
    val (indicators, buttons) = config
    val finalState = combo.zip(buttons).fold(List(indicators.size) { 0 }) { acc, (c, indicators) ->
        acc.mapIndexed { index, n ->
            if (indicators.contains(index)) n + c else n
        }
    }
    val isGood = finalState.zip(indicators).all { (f, i) -> f % 2 == i }
    return isGood
}

fun getPressPermutations(buttons: Int, totalPresses: Int): Set<List<Int>> {
    val key = Pair(buttons, totalPresses)
    return if (pressCombinations.containsKey(key)) pressCombinations[key]!!
    else {
        val answer =
            if (totalPresses == 0) setOf(List(buttons) { 0 })
            else getPressPermutations(buttons, totalPresses - 1).flatMap { combo ->
                combo.indices.map { i ->
                    combo.mapIndexed { j, c ->
                        if (i == j) c + 1 else c
                    }
                }
            }.toSet()
        pressCombinations[key] = answer
        answer
    }
}

val pressCombinations = mutableMapOf<Pair<Int, Int>, Set<List<Int>>>()

typealias ButtonConfig = Triple<List<Int>, List<List<Int>>, List<Int>>

fun parse(input: String): ButtonConfig {
    val indicators = """\[(.*)\]""".toRegex().find(input)!!.groups[1]!!.value.toList().map { if (it == '.') 0 else 1 }
    val wiringSchematics =
        """\(([\d,]*\d)\)""".toRegex().findAll(input).map { it.groups[1]!!.value.split(",").map(String::toInt) }
            .toList()
    val joltageRequirements = """\{(.*)\}""".toRegex().find(input)!!.groups[1]!!.value.split(",").map(String::toInt)
    return Triple(indicators, wiringSchematics, joltageRequirements)
}
