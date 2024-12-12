package days.day12

import kotlin.system.exitProcess
import lib.Grid
import lib.Reader
import lib.Vector
import lib.atOrNull
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day12/input.txt").grid()
    val exampleInput = Reader("/day12/example-1.txt").grid()

//    part1(input).checkAnswer(12)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1485656)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(grid: Grid<Char>) =
    grid.getRegions().sumOf { it.getFenceCost1() }

fun part2(grid: Grid<Char>) =
    grid.getRegions().sumOf { it.getFenceCost2() }

fun Grid<Char>.getRegions(): List<Set<Vector>> {
    val visited = mutableSetOf<Vector>()
    val regions = mutableListOf<Set<Vector>>()
    cells().forEach { (point, value) ->
        if (point !in visited) {
            val region = mutableSetOf(point)
            var frontier = listOf(point)
            while (frontier.isNotEmpty()) {
                val newFrontier = frontier.flatMap { it.neighbours }.distinct()
                    .filter { atOrNull(it) == value && it !in region }
                region += newFrontier
                frontier = newFrontier
                visited += newFrontier
            }
            regions += region
        }
    }

    return regions
}

fun Set<Vector>.getFenceCost1() =
    sumOf { (it.neighbours - this).size } * size

fun Set<Vector>.getFenceCost2() =
    sumOf { (it.neighbours - this).size } * size
