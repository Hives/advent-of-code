package days.day20

import lib.Grid
import lib.Reader
import lib.Vector
import lib.atOrNull
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day20/input.txt").grid()
    val exampleInput = Reader("/day20/example-1.txt").grid()

    time(message = "Part 1", warmUp = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(1293)

    time(message = "Part 2", warmUp = 5, iterations = 5) {
        part2(input)
    }.checkAnswer(977747)
}

fun part1(input: Grid<Char>) =
    doIt(input, 2)

fun part2(input: Grid<Char>) =
    doIt(input, 20)

fun doIt(input: Grid<Char>, maxCheatDistance: Int): Int {
    val (start, end, grid) = parseInput(input)
    val path = getPath(start, end, grid)
    val totalTime = path.size

    return path.indices.sumOf { i ->
        val pathPoint1 = path[i]
        (i + 1).rangeUntil(path.size).count { j ->
            val pathPoint2 = path[j]
            val cheatDistance = getCheat(pathPoint1.point, pathPoint2.point, maxCheatDistance)
            if (cheatDistance == null) {
                false
            } else {
                val time = pathPoint1.distanceFromStart + pathPoint2.distanceFromEnd + cheatDistance
                val saving = totalTime - time
                saving >= 100
            }
        }
    }
}

fun getPath(start: Vector, end: Vector, grid: Grid<Char>): List<PathData> {
    val path = mutableListOf(start)
    var current = start
    while (current != end) {
        val next = current.neighbours
            .single { neighbour ->
                grid.atOrNull(neighbour).let {
                    it == '.' && neighbour != path.getOrNull(path.size - 2)
                }
            }
        current = next
        path += current
    }
    return path.indices.map { i ->
        val current = path[i]
        PathData(current, i, path.size - i)
    }
}

fun getCheat(p1: Vector, p2: Vector, maxDistance: Int): Int? =
    ((p2 - p1).manhattanDistance).let { if (it <= maxDistance) it else null }

data class PathData(
    val point: Vector,
    val distanceFromStart: Int,
    val distanceFromEnd: Int
)

fun parseInput(input: Grid<Char>): Triple<Vector, Vector, List<List<Char>>> {
    val start = input.cells().single { (_, cell) -> cell == 'S' }.first
    val end = input.cells().single { (_, cell) -> cell == 'E' }.first
    val grid = input.map { row ->
        row.map { cell ->
            if (cell == '#') cell else '.'
        }
    }
    return Triple(start, end, grid)
}
