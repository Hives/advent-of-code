package days.day12

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day12/input.txt").string()
    val (part1) = Reader("/day12/answers.txt").ints()
    val exampleInput = Reader("/day12/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)
}

fun part1(input: String): Int {
    val (presents, regions) = parseInput(input)
    val presentSize = presents.map { it.sumOf { it.count { it == '#' } } }
    return regions.count {
        val presentSize = it.second.zip(presentSize).fold(0) { acc, (size, count) -> acc + (size * count) }
        val size = it.first.first * it.first.second
        presentSize <= size
    }
}

fun parseInput(input: String) =
    input.split("\n\n").let {
        val presents = it.dropLast(1).map {
            it.lines().drop(1).map { it.toList() }
        }
        val regions = it.last().lines().map {
            it.split(": ").let {
                Pair(
                    it[0].split("x").let { Pair(it[0].toInt(), it[1].toInt()) },
                    it[1].split(" ").map { it.toInt() }
                )
            }
        }
        Pair(presents, regions)
    }
