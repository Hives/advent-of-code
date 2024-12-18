package days.day18

import kotlin.system.exitProcess
import lib.CompassDirection
import lib.Grid
import lib.Reader
import lib.Vector
import lib.atOrNull
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day18/input.txt").vectors()
    val exampleInput = Reader("/day18/example-1.txt").vectors()

    time(message = "Part 1", iterations = 20, warmUp = 20) {
        part1(input, 70, 1024)
    }.checkAnswer(298)

    time(message = "Part 2", iterations = 5, warmUp = 5) {
        part2(input, 70)
    }.checkAnswer(Vector(52, 32))
}

fun part1(input: List<Vector>, gridSize: Int, bytes: Int) =
    aStar(
        start = Vector(0, 0),
        goal = Vector(gridSize, gridSize),
        grid = createGrid(input, gridSize, bytes)
    )

fun part2(input: List<Vector>, gridSize: Int): Vector {
    var min = 0
    var max = input.size

    while (max > min) {
        val mid = min + ((max - min) / 2)
        val result = aStar(
            start = Vector(0, 0),
            goal = Vector(gridSize, gridSize),
            grid = createGrid(input, gridSize, mid)
        )
        if (result == null) max = mid - 1
        else min = mid + 1
    }
    
    return input[max - 1]
}

fun createGrid(input: List<Vector>, gridSize: Int, bytes: Int) =
    (0..gridSize).map { y ->
        (0..gridSize).map { x ->
            if (Vector(x, y) in input.take(bytes)) '#' else '.'
        }
    }

fun aStar(start: Vector, goal: Vector, grid: Grid<Char>): Int? {
    fun h(point: Vector) = (goal - point).manhattanDistance

    val openSet = mutableSetOf(start)
    val cameFrom = mutableMapOf<Vector, Vector>()
    val gScore = mutableMapOf<Vector, Int>()
    gScore[start] = 0
    val fScore = mutableMapOf<Vector, Int>()
    fScore[start] = h(start)

    fun reconstructPath(end: Vector): List<Vector> {
        var current = end
        val path = mutableListOf(current)
        while (current in cameFrom.keys) {
            current = cameFrom[current]!!
            path.add(current)
        }
        return path
    }

    while (openSet.isNotEmpty()) {
        val current = openSet.minBy { fScore.getOrDefault(it, Int.MAX_VALUE) }
        if (current == goal) {
            return reconstructPath(current).size - 1
        }
        openSet.remove(current)
        val neighbours = CompassDirection.entries.map { current + it }
            .filter { grid.atOrNull(it) == '.' }
        neighbours.forEach { neighbour ->
            val tentativeGScore = gScore[current]!! + 1
            if (tentativeGScore < gScore.getOrDefault(neighbour, Int.MAX_VALUE)) {
                cameFrom[neighbour] = current
                gScore[neighbour] = tentativeGScore
                fScore[neighbour] = tentativeGScore + h(neighbour)
                if (neighbour !in openSet) {
                    openSet.add(neighbour)
                }
            }
        }
    }

    return null
}
