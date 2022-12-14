package days.day14

import lib.AllCompassDirection
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    val input = Reader("day14.txt").strings()
    val exampleInput = Reader("day14-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(805)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(25161)
}

fun part1(input: List<String>): Int {
    val grid = parse(input)
    do {
        val final = grid.produce()
    } while (final != null)
    return grid.count()
}

fun part2(input: List<String>): Int {
    val grid = parse(input, addFloor = true)
    do {
        val final = grid.produce()
    } while (final != Vector(500, 0))
    return grid.count()
}

fun parse(input: List<String>, addFloor: Boolean = false): Cave {
    val paths = input.map { line ->
        line.split(" -> ")
            .map { point ->
                point.split(",")
                    .let { (x, y) -> Vector(x.toInt(), y.toInt()) }
            }
    }

    val (maxX, minX, maxY) = paths.flatten().let { points ->
        val ys = points.map { it.y }
        val xs = points.map { it.x }

        if (addFloor) {
            val maxY = ys.max() + 3
            val maxX = max(xs.max() + 1, 500 + maxY)
            val minX = min(xs.min() + 1, 500 - maxY)
            Triple(maxX, minX, maxY)
        } else {
            Triple(xs.max() + 1, xs.min() - 1, ys.max() + 1)
        }
    }

    val grid = Cave(
        mutableGrid = MutableList(maxY + 1) { MutableList(maxX - minX + 1) { AIR } },
        minX = minX
    )

    paths.forEach { path ->
        path.windowed(2, 1).forEach { (start, end) ->
            start.pathTo(end).forEach { grid.set(it, ROCK) }
        }
    }

    if (addFloor) {
        grid.addFloor()
    }

    return grid
}

data class Cave(private val mutableGrid: MutableList<MutableList<Char>>, val minX: Int) {
    val grid: List<List<Char>>
        get() = mutableGrid.map { it.toList() }

    fun print() = mutableGrid.forEach { println(it.joinToString("")) }

    fun set(point: Vector, char: Char = SAND) {
        mutableGrid[point.y][point.x - minX] = char
    }

    fun at(point: Vector) = mutableGrid[point.y][point.x - minX]

    fun produce(): Vector? {
        val start = Vector(500, 0)

        fun fall(point: Vector): Vector? {
            return when {
                point.y >= mutableGrid.size - 1 -> null
                at(point + down) == AIR -> fall(point + down)
                at(point + downLeft) == AIR -> fall(point + downLeft)
                at(point + downRight) == AIR -> fall(point + downRight)
                else -> point
            }
        }

        val final = fall(start)

        if (final != null) {
            set(final)
        }

        return final
    }

    fun count() = mutableGrid.flatten().count { it == SAND }

    fun addFloor() {
        val maxY = mutableGrid.indexOfLast { row -> row.any { it == ROCK } }
        mutableGrid[maxY + 2].indices.forEach { x -> mutableGrid[maxY + 2][x] = ROCK }
    }

    private val down = AllCompassDirection.N.vector
    private val downLeft = AllCompassDirection.NW.vector
    private val downRight = AllCompassDirection.NE.vector
}

const val AIR = '.'
const val SAND = 'o'
const val ROCK = '#'
