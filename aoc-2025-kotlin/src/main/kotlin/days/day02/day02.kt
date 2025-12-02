package days.day02

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day02/input.txt").string()
    val (part1, part2) = Reader("/day02/answers.txt").longs()
    val exampleInput = Reader("/day02/example-1.txt").string()

    time(warmUp = 5, iterations = 5, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    // slow - ~1.5s
    time(warmUp = 2, iterations = 2, message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: String): Long =
    parseInput(input).flatten().filter {
        val s = it.toString()
        s.take(s.length / 2) == s.drop(s.length / 2)
    }.sum()

fun part2(input: String): Long =
    parseInput(input).flatten().filter {
        val s = it.toString()
        for (n in 1..(s.length / 2)) {
            if (s.chunked(n).toSet().size == 1) return@filter true
        }
        false
    }.sum()

fun parseInput(input: String): List<LongRange> =
    input.split(",").map { range -> range.split("-").let { LongRange(it[0].toLong(), it[1].toLong()) } }
