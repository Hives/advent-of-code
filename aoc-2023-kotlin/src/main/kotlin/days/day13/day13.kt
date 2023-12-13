package days.day13

import days.day11.transpose
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day13/input.txt").string()
    val exampleInput = Reader("/day13/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(27664)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(33991)
}

fun part1(input: String): Int =
    parse(input).sumOf { it.summarize() }

fun part2(input: String) =
    parse(input).sumOf { it.summarizeAfterRemovingSmudge() }

fun Grid.summarize(): Int {
    val horizontal = findHorizontalReflectionLine()
    val vertical = transpose().findHorizontalReflectionLine()
    return if (horizontal != null) 100 * (horizontal + 1) else vertical!! + 1
}

fun Grid.summarizeAfterRemovingSmudge(): Int {
    val horizontal = findNewHorizontalReflectionLineAfterRemovingSmudge()
    val vertical = transpose().findNewHorizontalReflectionLineAfterRemovingSmudge()
    return if (horizontal != null) 100 * (horizontal + 1) else vertical!! + 1
}

fun Grid.findNewHorizontalReflectionLineAfterRemovingSmudge(): Int? {
    val existingHorizontalReflectionLine = findHorizontalReflectionLine()
    val smudgeCandidates = findHorizontalSmudgeCandidates()
    return smudgeCandidates.mapNotNull { smudge ->
        val newGrid = removeSmudge(smudge)
        val line = newGrid.findHorizontalReflectionLine(existingHorizontalReflectionLine)
        if (line != null && line != existingHorizontalReflectionLine) {
            line
        } else {
            null
        }
    }.also {
        if (it.size > 1) throw Error("Oh no?!")
    }.singleOrNull()
}

fun Grid.findHorizontalSmudgeCandidates(): Set<Vector> {
    val candidates = mutableSetOf<Vector>()
    indices.forEach { y1 ->
        val row1 = this[y1]
        ((y1 + 1) until size).forEach { y2 ->
            val row2 = this[y2]
            val differences = differInPlaces(row1, row2)
            if (differences.size == 1) {
                val x = differences.single()
                if (row1[x] == '#') candidates.add(Vector(x, y1))
                else candidates.add(Vector(x, y2))
            }
        }
    }
    return candidates
}

fun Grid.findHorizontalReflectionLine(butNotThisOne: Int? = null): Int? =
    (0..this.size - 2).firstOrNull { n ->
        if (n == butNotThisOne) return return@firstOrNull false
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
    }

fun Grid.removeSmudge(smudge: Vector) =
    mapIndexed { y, row ->
        if (y == smudge.y) {
            row.mapIndexed { x, char ->
                if (x == smudge.x) '.' else char
            }
        } else {
            row
        }
    }

fun Grid.printy() = forEach { println(it.joinToString("")) }

fun <T> differInPlaces(xs: List<T>, ys: List<T>): List<Int> =
    xs.indices.mapNotNull { index ->
        if (xs[index] != ys[index]) index else null
    }

fun parse(input: String): List<Grid> =
    input.split("\n\n").map { it.split("\n").map(String::toList) }

typealias Grid = List<List<Char>>
