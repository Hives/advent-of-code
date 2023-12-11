package days.day11

import lib.Reader
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
    getExpandedGalaxies(input, 2).getPairs()
        .sumOf { it.manhattanDistance() }

fun part2(input: List<List<Char>>): Long =
    getExpandedGalaxies(input, 1_000_000).getPairs()
        .sumOf { it.manhattanDistance() }

fun getExpandedGalaxies(grid: List<List<Char>>, expansionIndex: Int): List<Pair<Long, Long>> {
    val emptyRowCounts = grid.getEmptyRowCounts()
    val emptyColCounts = grid.transpose().getEmptyRowCounts()

    return grid.getGalaxies().map { (x, y) ->
        Pair(
            x + (emptyColCounts[x.toInt()] * (expansionIndex - 1)),
            y + (emptyRowCounts[y.toInt()] * (expansionIndex - 1))
        )
    }
}

fun List<List<Char>>.getEmptyRowCounts() =
    fold(emptyList<Int>()) { acc, row ->
        val add = if (row.all { it == '.' }) (acc.lastOrNull() ?: 0) + 1
        else acc.lastOrNull() ?: 0
        acc + add
    }


fun List<List<Char>>.getGalaxies(): List<Pair<Long, Long>> =
    flatMapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (c == '#') Pair(x.toLong(), y.toLong()) else null
        }
    }.filterNotNull().toList()

fun List<Pair<Long, Long>>.getPairs(): List<Pair<Pair<Long, Long>, Pair<Long, Long>>> =
    indices.flatMap { first ->
        ((first + 1) until this.size).map { second ->
            Pair(this[first], this[second])
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

fun Pair<Pair<Long, Long>, Pair<Long, Long>>.manhattanDistance(): Long =
    let { (point1, point2) ->
        abs(point1.first - point2.first) + abs(point1.second - point2.second)
    }
