package days.day10

import lib.Grid
import lib.Reader
import lib.Vector
import lib.atOrNull
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day10/input.txt").intGrid()
    val exampleInput = Reader("/day10/example-1.txt").intGrid()

    time(message = "Part 1", warmUp = 100) {
        part1(input)
    }.checkAnswer(538)

    time(message = "Part 2", warmUp = 100) {
        part2(input)
    }.checkAnswer(1110)
}

fun part1(grid: Grid<Int>) =
    grid.getTrailheads().sumOf { trailhead ->
        grid.findHikingTrails(trailhead)
            .map { it.last() }
            .distinct()
            .count()
    }

fun part2(grid: Grid<Int>) =
    grid.getTrailheads().sumOf {
        grid.findHikingTrails(it).count()
    }

fun Grid<Int>.getTrailheads() = cells()
    .filter { (_, value) -> value == 0 }
    .map { it.first }

fun Grid<Int>.findHikingTrails(trailhead: Vector): List<List<Vector>> {
    fun go(path: List<Vector>): List<List<Vector>> {
        val previous = atOrNull(path.last())!!
        return if (previous == 9) {
            listOf(path)
        } else {
            path.last().neighbours
                .filter { atOrNull(it) == previous + 1 }
                .flatMap { go(path + it) }
        }
    }

    return go(listOf(trailhead))
}
