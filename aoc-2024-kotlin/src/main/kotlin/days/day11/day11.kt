package days.day11

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day11/input.txt").listOfLongs()
    val exampleInput = Reader("/day11/example-1.txt").listOfLongs()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(185205)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(221280540398419)
}

fun part1(input: List<Long>) =
    solve(input, 25)

fun part2(input: List<Long>) =
    solve(input, 75)

fun solve(stones: List<Long>, blinks: Int) =
    stones.sumOf { solveSingle(it, blinks) }

val solveSingleCache = mutableMapOf<Pair<Long, Int>, Long?>().withDefault { null }

fun solveSingle(stone: Long, blinks: Int): Long {
    val cacheKey = Pair(stone, blinks)
    solveSingleCache.getValue(cacheKey)?.let { return it }

    val result =
        if (blinks == 0) 1L
        else expandStone(stone).sumOf { solveSingle(it, blinks - 1) }

    return result.also { solveSingleCache[cacheKey] = it }
}

fun expandStone(stone: Long): List<Long> =
    when {
        stone == 0L -> listOf(1)
        stone.countDigits().isEven() -> stone.split()
        else -> listOf(stone * 2024)
    }

fun Long.split(): List<Long> {
    val s = this.toString()
    val half = s.length / 2
    return listOf(s.substring(0, half).toLong(), s.substring(half, s.length).toLong())
}

fun Long.countDigits() =
    this.toString().length

fun Int.isEven() =
    this % 2 == 0
