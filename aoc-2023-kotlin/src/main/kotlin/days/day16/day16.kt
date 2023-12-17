package days.day16

import days.day16.Direction.Down
import days.day16.Direction.Left
import days.day16.Direction.Right
import days.day16.Direction.Up
import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day16/input.txt").grid()
    val exampleInput = Reader("/day16/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(6978)

    time(message = "Part 2", warmUpIterations = 2, iterations = 2) {
        part2(input)
    }.checkAnswer(7315)
}

private fun part1(grid: Grid) =
    grid.countEnergised(MovingPoint(Vector(0, 0), Right))

private fun part2(grid: Grid): Int {
    val top = grid[0].indices.map { MovingPoint(Vector(it, 0), Down) }
    val bottom = grid[0].indices.map { MovingPoint(Vector(it, grid.size - 1), Up) }
    val left = grid.indices.map { MovingPoint(Vector(0, it), Right) }
    val right = grid.indices.map { MovingPoint(Vector(grid[0].size - 1, it), Left) }
    return listOf(top, bottom, left, right).flatten().maxOf { grid.countEnergised(it) }
}

private fun Grid.countEnergised(start: MovingPoint): Int {
    var current = listOf(start)
    val energised = mutableSetOf(start)

    while (current.isNotEmpty()) {
        val next = current.flatMap { getNext(it) }
        val nextWeHaventSeenBefore = next - energised
        energised.addAll(nextWeHaventSeenBefore)
        current = nextWeHaventSeenBefore
    }

    return energised.map { it.point }.toSet().size
}

private fun Grid.getNext(movingPoint: MovingPoint): List<MovingPoint> {
    val (point, direction) = movingPoint
    val nextDirections = when (at(point)) {
        '.' -> listOf(direction)
        '|' -> {
            if (direction == Left || direction == Right) listOf(Up, Down)
            else listOf(direction)
        }

        '-' -> {
            if (direction == Up || direction == Down) listOf(Left, Right)
            else listOf(direction)
        }

        '/' -> {
            when (direction) {
                Down -> listOf(Left)
                Left -> listOf(Down)
                Right -> listOf(Up)
                Up -> listOf(Right)
            }
        }

        '\\' -> {
            when (direction) {
                Down -> listOf(Right)
                Left -> listOf(Up)
                Right -> listOf(Down)
                Up -> listOf(Left)
            }
        }

        else -> throw Error("Uh oh")
    }

    return nextDirections.map { MovingPoint(point + it, it) }.filter { at(it.point) != null }
}

private fun Grid.printy(energised: Set<MovingPoint>) {
    val energisedPoints = energised.map { it.point }.toSet()
    indices.map { y ->
        this[y].mapIndexed { x, c ->
            if (Vector(x, y) in energisedPoints) '#' else c
        }.joinToString("")
            .also { println(it) }
    }
}

private operator fun Vector.plus(other: Direction) = Vector(
    x = this.x + other.v.x,
    y = this.y + other.v.y,
)

private sealed class Direction(val v: Vector) {
    object Left : Direction(Vector(-1, 0))
    object Right : Direction(Vector(1, 0))
    object Up : Direction(Vector(0, -1))
    object Down : Direction(Vector(0, 1))
}

private fun Grid.at(v: Vector) =
    if (v.y < 0 || v.y >= this.size || v.x < 0 || v.x >= this[v.y].size) null
    else this[v.y][v.x]

private data class MovingPoint(val point: Vector, val direction: Direction)
