package days.day13

import days.day11.transpose
import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day13/input.txt").string()
    val exampleInput = Reader("/day13/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(27664)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String): Int =
    parse(input).sumOf { grid ->
        grid.findHorizontalReflectionLine()?.let { 100 * it }
            ?: grid.transpose().findHorizontalReflectionLine()!!
    }

fun part2(input: String): Int {
    return -1
}

fun Grid.findHorizontalReflectionLine(): Int? {
    return (0..this.size - 2).firstOrNull { n ->
        var above = n
        var below = n + 1
        while (above >= 0 && below < size) {
            if (this[above] != this[below]) {
                return@firstOrNull false
            }
            above -= 1
            below += 1
        }
        true
    }?.let { it + 1 }
}

fun parse(input: String): List<Grid> =
    input.split("\n\n").map { it.split("\n").map(String::toList) }

typealias Grid = List<List<Char>>
