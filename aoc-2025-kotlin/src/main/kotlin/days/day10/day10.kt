package days.day10

import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day10/input.txt").strings()
    val (part1, part2) = Reader("/day10/answers.txt").ints()
    val exampleInput = Reader("/day10/example-1.txt").strings()

//    part2(listOf("[##.#.#####] (2,3,7,8,9) (0,8) (0) (1,3,5,6,7,8) (0,2,3,4,8) (2,3,6,9) (0,3,4) (6,8) (5) (0,2,5,7,9) (0,1,3,4,5,6,7,9) (1,2,3,6,7,8,9) {45,36,41,62,20,34,51,51,33,55}"))

//    part2(listOf("[..##.....] (0,1,4,5,7) (0,1,3,4,5,6,7) (3,4,5,6,8) (0,1,2,3,7,8) (0,1,2,3,4,5,7) (2,5,7,8) (2,5,6,7,8) (0,1,2,3,6,7,8) (0,1,2,4,5,6) (0,1,4,8) {79,79,77,44,65,79,40,80,66}"))


    part2(input)
    exitProcess(0)


    time(warmUp = 10, iterations = 10, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

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
    val emptyPossibility = List<Int?>(13) { null }

    val requirementsMaps = input.asSequence().map {
        println(it)
        parse(it)
    }.map { requirement ->
        val (_, wirings, joltageTargets) = requirement
        joltageTargets.mapIndexed { targetIndex, targetJoltage ->
            val buttons = wirings.mapIndexedNotNull { wiringIndex, wiring ->
                if (wiring.contains(targetIndex)) wiringIndex else null
            }
            targetJoltage to buttons
        }
            .sortedBy { it.second.size }
            .sortedBy { it.first }
    }.sumOf { reqs ->
        var possibilities = listOf(emptyPossibility)

        reqs.indices.forEach { i ->
//            println("--")
            val newPossibilities = possibilities.mapNotNull { expandPossibility(it, reqs[i]) }.flatten()
//            println(newPossibilities.size)
            val filteredPossibilities = filterPossibilities(newPossibilities, reqs.drop(i + 1))
//            println(filteredPossibilities.size)
            possibilities = filteredPossibilities
        }

        possibilities.minOf { it.filterNotNull().sum() }.also(::println)
    }.also(::println)

    return -1
}

fun filterPossibilities(possibilities: List<List<Int?>>, requirements: List<Pair<Int, List<Int>>>): List<List<Int?>> {
    return possibilities.filter { possibility -> requirements.all { req -> test(possibility, req) } }
}

fun test(possibility: List<Int?>, requirement: Pair<Int, List<Int>>): Boolean {
    val (target, buttons) = requirement
    val joltageAchieved = possibility.filterIndexed { index, _ -> buttons.contains(index) }.filterNotNull().sum()
    return joltageAchieved <= target
}

fun expandPossibility(possibility: List<Int?>, requirement: Pair<Int, List<Int>>): List<List<Int?>>? {
    val (target, buttons) = requirement
    val joltageAchievedAlready = possibility.filterIndexed { index, _ -> buttons.contains(index) }.filterNotNull().sum()
    val remainingTarget = target - joltageAchievedAlready
    if (remainingTarget < 0) return null
    val remainingButtons = buttons.filter { possibility[it] == null }

    val newPossibilities = getPossibilities(remainingTarget, remainingButtons)
    return newPossibilities.map {
        List(13) { button -> if (button in it) it[button] else possibility[button] }
    }
}

fun getPossibilities(target: Int, buttons: List<Int>): List<Map<Int, Int>> {
    return CombsWithReps(target, buttons.size, buttons).combinations.map { combination ->
        buttons.map { button -> button to combination.count { it == button } }.toMap()
    }
}

// copied this part off the internet
class CombsWithReps<T>(val m: Int, val n: Int, val items: List<T>) {
    private val combination = IntArray(m)
    private val combinationsInternal = mutableSetOf<List<T>>()
    val combinations: Set<List<T>> = combinationsInternal
    private var count = 0

    init {
        generate(0)
    }

    private fun generate(k: Int) {
        if (k >= m) {
            val list = mutableListOf<T>()
            for (i in 0 until m) {
                val t = items[combination[i]]
                list.add(t)
            }
            combinationsInternal.add(list)
            count++
        } else {
            for (j in 0 until n)
                if (k == 0 || j >= combination[k - 1]) {
                    combination[k] = j
                    generate(k + 1)
                }
        }
    }
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
