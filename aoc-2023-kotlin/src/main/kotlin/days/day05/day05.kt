package days.day05

import lib.Reader
import lib.checkAnswer
import lib.overlap
import lib.shift
import lib.subtract
import lib.time
import lib.toLongs

fun main() {
    val input = Reader("/day05/input.txt").string()
    val exampleInput = Reader("/day05/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(993500720)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(4917124)
}

fun part1(input: String): Long {
    val (seeds, _, maps) = parse(input)

    return seeds.minOf { seed ->
        maps.fold(seed) { acc, map -> map.apply(acc) }
    }
}

fun part2(input: String): Long {
    val (_, seedRanges, maps) = parse(input)

    return seedRanges.minOf { seedRange ->
        maps.fold(listOf(seedRange)) { acc, map ->
            map.apply(acc)
        }.minOf { it.first }
    }
}

class Map(
    values: List<List<Long>>
) {
    private val lines = values.map { Line(it[0], it[1], it[2]) }

    fun apply(input: Long): Long =
        lines.firstNotNullOfOrNull { it.apply(input) } ?: input

    fun apply(input: List<LongRange>): List<LongRange> =
        input.flatMap { apply(it) }

    private fun apply(input: LongRange): List<LongRange> {
        val transformed = lines.mapNotNull { it.apply(input) }
        val untransformed = lines.fold(listOf(input)) { acc, line ->
            acc.flatMap { it.subtract(line.sourceRange) }
        }
        return transformed + untransformed
    }

    private class Line(
        destRangeStart: Long,
        sourceRangeStart: Long,
        rangeLength: Long
    ) {
        val sourceRange = sourceRangeStart until sourceRangeStart + rangeLength
        private val translation = destRangeStart - sourceRangeStart

        fun apply(input: Long): Long? =
            if (sourceRange.contains(input)) input + translation
            else null

        fun apply(input: LongRange) =
            (input.overlap(sourceRange))?.shift(translation)
    }
}

fun parse(input: String): Triple<List<Long>, List<LongRange>, List<Map>> {
    val chunks = input.split("\n\n")
    val seeds = chunks[0].split(": ")[1].toLongs()
    val seedRanges = seeds.chunked(2).map { it[0]..(it[0] + it[1]) }
    val maps = chunks.drop(1).map { chunk ->
        chunk.lines().drop(1).map(String::toLongs).let(::Map)
    }
    return Triple(seeds, seedRanges, maps)
}
