package days.day11

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.abs

fun main() {
    val input = Reader("/day11/input.txt").grid()
    val exampleInput = Reader("/day11/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(9521550)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(298932923702)
}

fun part1(input: List<List<Char>>): Long =
    input.getExpandedGalaxies(2).getAllPairs()
        .sumOf { it.manhattanDistance() }

fun part2(input: List<List<Char>>): Long =
    input.getExpandedGalaxies(1_000_000).getAllPairs()
        .sumOf { it.manhattanDistance() }

fun List<List<Char>>.getExpandedGalaxies(expansionIndex: Int): List<Vector> {
    val emptyRowCounts = getEmptyRowCounts()
    val emptyColCounts = transpose().getEmptyRowCounts()

    return getGalaxies().map { (x, y) ->
        Vector(
            x + (emptyColCounts[x] * (expansionIndex - 1)),
            y + (emptyRowCounts[y] * (expansionIndex - 1))
        )
    }
}

fun List<List<Char>>.getEmptyRowCounts() =
    fold(emptyList<Int>()) { acc, row ->
        val add = if (row.all { it == '.' }) (acc.lastOrNull() ?: 0) + 1
        else acc.lastOrNull() ?: 0
        acc + add
    }

fun List<List<Char>>.getGalaxies(): List<Vector> =
    flatMapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (c == '#') Vector(x, y) else null
        }
    }.filterNotNull().toList()

fun <T> List<T>.getAllPairs(): List<Pair<T, T>> =
    flatMapIndexed { index, first ->
        subList(index + 1, this.size).map { second ->
            Pair(first, second)
        }
    }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val ys = this.indices
    val xs = this[0].indices

    return xs.map { x ->
        ys.map { y ->
            this[y][x]
        }
    }
}

fun Pair<Vector, Vector>.manhattanDistance(): Long =
    let { (point1, point2) ->
        abs(point1.x - point2.x).toLong() + abs(point1.y - point2.y).toLong()
    }
