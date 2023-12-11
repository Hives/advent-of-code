package days.day11

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day11/input.txt").grid()
    val exampleInput = Reader("/day11/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(9521550)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<List<Char>>): Int =
    input.expand().getGalaxies().getPairs()
        .sumOf { (it.second - it.first).manhattanDistance }

fun part2(input: List<List<Char>>): Int {
    return -1
}

fun List<List<Char>>.expand() =
    expandHorizontal().transpose().expandHorizontal().transpose()

fun List<List<Char>>.getGalaxies(): List<Vector> =
    flatMapIndexed { y, row ->
        row.mapIndexed { x, c ->
            if (c == '#') Vector(x, y) else null
        }
    }.filterNotNull().toList()

fun List<Vector>.getPairs(): List<Pair<Vector, Vector>> =
    indices.flatMap { first ->
        ((first + 1) until this.size).map { second ->
            Pair(this[first], this[second])
        }
    }

fun List<List<Char>>.expandHorizontal(): List<List<Char>> =
    this.flatMap { row ->
        if (row.all { it == '.' }) listOf(row, row)
        else listOf(row)
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
