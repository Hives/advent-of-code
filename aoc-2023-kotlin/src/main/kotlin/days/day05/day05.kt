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
    val (seeds, maps) = parse(input)

    return seeds.minOf { seed ->
        maps.fold(seed) { acc, map -> map.apply(acc) }
    }
}

fun part2(input: String): Int {
    return -1
}

fun parse(input: String): Pair<List<Long>, List<Map>> {
    val splitted = input.split("\n\n")
    val seeds = splitted[0].split(": ")[1].split(" ").map(String::toLong)
    val maps = splitted.drop(1).map { Map(it) }
    return Pair(seeds, maps)
}

data class Map(
    private val input: String
) {
    private val lines =
        input.lines().drop(1).map { line -> line.split(" ").map(String::toLong).let { Line(it[0], it[1], it[2]) } }

    fun apply(input: Long): Long =
        lines.firstNotNullOfOrNull { it.apply(input) } ?: input

    private data class Line(
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
}


