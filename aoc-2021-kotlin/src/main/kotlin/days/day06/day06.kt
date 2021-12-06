package days.day06

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day06.txt").string()
    val exampleInput = Reader("day06-example.txt").string()

    time(iterations = 10_000, warmUpIterations = 10_000, message = "Part 1") {
        part1(input)
    }.checkAnswer(356190L)

    time(iterations = 10_000, warmUpIterations = 10_000, message = "Part 2") {
        part2(input)
    }.checkAnswer(1617359101538L)
}

fun part1(input: String) = runGenerations(parseInput(input), 80)
fun part2(input: String) = runGenerations(parseInput(input), 256)

fun runGenerations(initialAges: List<Long>, requiredDays: Int): Long {
    tailrec fun go(ages: List<Long>, days: Int): Long =
        if (days == 0) ages.sum()
        else {
            val newAges = ages.rotateLeft().toMutableList()
            newAges[6] += ages[0]
            go(newAges.toList(), days - 1)
        }
    return go(initialAges, requiredDays)
}

fun parseInput(input: String): List<Long> =
    input.split(",").map { it.toInt() }
        .groupingBy { it }.eachCount()
        .let { agesCount ->
            val agesList = MutableList(9) { 0L }
            agesCount.forEach { (age, count) ->
                agesList[age] = count.toLong()
            }
            agesList.toList()
        }

fun <T> List<T>.rotateLeft() = this.subList(1, this.size) + this[0]
