package days.day05

import lib.Reader
import lib.checkAnswer
import lib.consolidate
import lib.time

fun main() {
    val input = Reader("/day05/input.txt").string()
    val (part1, part2) = Reader("/day05/answers.txt").mapAnswers(String::toInt, String::toLong)
    val exampleInput = Reader("/day05/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: String): Int {
    val (ranges, ids) = parseInput(input)
    return ids.count { id ->
        ranges.any { range -> range.contains(id) }
    }
}

fun part2(input: String): Long {
    val (ranges, _) = parseInput(input)
    return ranges.consolidate().sumOf { it.last - it.first + 1 }
}

fun parseInput(input: String): Pair<List<LongRange>, List<Long>> {
    val (a, b) = input.split("\n\n")
    return Pair(
        a.lines().map { line -> line.split("-").let { it[0].toLong()..it[1].toLong() } },
        b.lines().map(String::toLong)
    )
}
