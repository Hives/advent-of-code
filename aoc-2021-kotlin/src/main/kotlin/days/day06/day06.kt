package days.day06

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day06.txt").string()
    val exampleInput = Reader("day06-example.txt").string()
    time {
        part1(input)
    }.checkAnswer(356190L)
    time {
        part2(input)
    }.checkAnswer(1617359101538L)
}

fun part1(input: String) = runGenerations(parseInput(input), 80)
fun part2(input: String) = runGenerations(parseInput(input), 256)

fun runGenerations(initialFish: Map<Int, Long>, requiredDays: Int): Long {
    fun go(fish: Map<Int, Long>, days: Int): Long {
        return if (days == 0) fish.values.sum()
        else {
            go(mapOf(
                0 to fish[1]!!,
                1 to fish[2]!!,
                2 to fish[3]!!,
                3 to fish[4]!!,
                4 to fish[5]!!,
                5 to fish[6]!!,
                6 to fish[7]!! + fish[0]!!,
                7 to fish[8]!!,
                8 to fish[0]!!,
            ), days - 1)
        }
    }
    return go(initialFish, requiredDays)
}

fun parseInput(input: String) =
    input.split(",").map { it.toInt() }
        .groupBy { it }
        .let {
            mapOf(
                0 to (it[0]?.size ?: 0).toLong(),
                1 to (it[1]?.size ?: 0).toLong(),
                2 to (it[2]?.size ?: 0).toLong(),
                3 to (it[3]?.size ?: 0).toLong(),
                4 to (it[4]?.size ?: 0).toLong(),
                5 to (it[5]?.size ?: 0).toLong(),
                6 to (it[6]?.size ?: 0).toLong(),
                7 to (it[7]?.size ?: 0).toLong(),
                8 to (it[8]?.size ?: 0).toLong(),
            )
        }