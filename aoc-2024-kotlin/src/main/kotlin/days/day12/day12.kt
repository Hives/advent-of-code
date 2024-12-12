package days.day12

import lib.CompassDirection
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

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1485656)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(899196)
}

fun part1(grid: Grid<Char>) =
    grid.getRegions().sumOf(::getFenceCost1)

fun part2(grid: Grid<Char>) =
    grid.getRegions().sumOf(::getFenceCost2)

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

fun getFenceCost1(region: Set<Vector>) =
    region.sumOf { (it.neighbours - region).size } * region.size

fun getFenceCost2(region: Set<Vector>): Int {
    val sides = CompassDirection.entries.map { dir ->
        region.map { point -> point + dir }.filter { it !in region }
    }
    return sides.sumOf { countContiguous(it) * region.size }
}

fun countContiguous(vs: List<Vector>): Int {
    val vsMutable = vs.toMutableSet()

    var count = 0
    while (vsMutable.isNotEmpty()) {
        val first = vsMutable.first()
        vsMutable.remove(first)
        val contiguous = mutableSetOf(first)
        var frontier = listOf(first)
        while (frontier.isNotEmpty()) {
            val newFrontier = frontier.flatMap { it.neighbours }.distinct().filter { it in vsMutable }
            contiguous += newFrontier
            vsMutable -= newFrontier.toSet()
            frontier = newFrontier
        }
        count += 1
    }
    return count
}
