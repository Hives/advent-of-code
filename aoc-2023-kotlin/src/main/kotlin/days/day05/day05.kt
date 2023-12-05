package days.day05

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day05/input.txt").string()
    val exampleInput = Reader("/day05/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(993500720)

//    time(message = "Part 2") {
//        part2(exampleInput)
//    }.checkAnswer(0)
}

fun part1(input: String): Long {
    val (seeds, mapses) = parse(input)

    return seeds.minOf { seed ->
        mapses.fold(seed) { acc, maps ->
            maps.firstNotNullOfOrNull { map ->
                map.apply(acc)
            } ?: acc
        }
    }
}

fun part2(input: String): Int {
    return -1
}

fun parse(input: String): Pair<List<Long>, List<List<Map>>> {
    val splitted = input.split("\n\n")
    val seeds = splitted[0].split(": ")[1].split(" ").map(String::toLong)
    val maps = splitted.drop(1).map { chunk ->
        chunk.lines().drop(1).map { line -> line.split(" ").map(String::toLong).let { Map(it[0], it[1], it[2]) } }
    }
    return Pair(seeds, maps)
}

data class Map(
    private val destRangeStart: Long,
    private val sourceRangeStart: Long,
    private val rangeLength: Long
) {
    private val sourceRange = sourceRangeStart..(sourceRangeStart + rangeLength - 1)
    private val translation = destRangeStart - sourceRangeStart

    fun apply(input: Long): Long? =
        if (sourceRange.contains(input)) input + translation
        else null
}
