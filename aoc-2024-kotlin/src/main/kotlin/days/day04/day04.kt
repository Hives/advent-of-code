package days.day04

import lib.AllCompassDirection
import lib.Grid
import lib.Reader
import lib.Vector
import lib.at
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day04/input.txt").grid()
    val exampleInput = Reader("/day04/example-1.txt").grid()

    time(message = "Part 1", iterations = 10) {
        part1(input)
    }.checkAnswer(2646)

    time(message = "Part 2", iterations = 10) {
        part2(input)
    }.checkAnswer(2000)
}

fun part1(grid: Grid<Char>) =
    grid.cells().sumOf { (point) ->
        AllCompassDirection.entries.toTypedArray().count { dir ->
            (0..3).map { point + dir.vector * it }
                .map { grid.at(it, '.') }
                .let { it == listOf('X', 'M', 'A', 'S') }
        }
    }

fun part2(grid: Grid<Char>): Int {
    val xPaths = listOf(
        (0..2).map { Vector(0, 0) + AllCompassDirection.NE.vector * it },
        (0..2).map { Vector(2, 0) + AllCompassDirection.NW.vector * it },
    )

    return grid.cells().count { (point) ->
        xPaths.map { path ->
            path.translate(point).map { grid.at(it, '.') }
        }.all {
            it == listOf('M', 'A', 'S') || it == listOf('S', 'A', 'M')
        }
    }
}

fun List<Vector>.translate(v: Vector) = map { it + v }
