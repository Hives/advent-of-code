package days.day15

import kotlin.system.exitProcess
import lib.CompassDirection
import lib.CompassDirection.E
import lib.CompassDirection.W
import lib.Grid
import lib.Reader
import lib.Vector
import lib.at
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day15/input.txt").string()
    val exampleInput1 = Reader("/day15/example-1.txt").string()
    val exampleInput2 = Reader("/day15/example-2.txt").string()
    val exampleInput3 = Reader("/day15/example-3.txt").string()

    part2(exampleInput2)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1415498)

    exitProcess(0)

    exitProcess(0)

    // 1426910 is too low
    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(inputString: String): Int {
    val input = parseInput(inputString)
    val (grid, things, moves) = input
    var robot = things.entries.single { it.value == "@" }.key

    moves.forEach { move ->
        if (move1(robot, move.vector, grid, things)) {
            robot += move.vector
        }
    }

    return things.score()
}

fun part2(input: String): Int {
    val (grid, things, moves) = parseInput2(input)
    var robot = things.entries.single { it.value == "@" }.key

    grid.printy(things)

    moves.forEach { move ->
        if (move2(robot, move.vector, grid, things)) {
            robot += move.vector
        }
        grid.printy(things)
    }

    return things.score2()
}

fun move1(point: Vector, direction: Vector, grid: Grid<String>, things: MutableMap<Vector, String>): Boolean {
    val newPoint = point + direction
    val destination = things[newPoint] ?: if (grid.at(newPoint, ".") == "#") "#" else null
    when (destination) {
        "#" -> {
            return false
        }

        null -> {
            things[newPoint] = things[point]!!
            things.remove(point)
            return true
        }

        else -> {
            if (move1(newPoint, direction, grid, things)) {
                things[newPoint] = things[point]!!
                things.remove(point)
                return true
            } else {
                return false
            }
        }
    }
}

fun move2(point: Vector, direction: Vector, grid: Grid<String>, things: MutableMap<Vector, String>): Boolean {
    println()
    val d = when (direction) {
        Vector(0, 1) -> "v"
        Vector(0, -1) -> "^"
        Vector(1, 0) -> ">"
        Vector(-1, 0) -> "<"
        else -> throw Error("asdasd")
    }
    println("Move: $d:")
    return if (canMove(point, direction, grid, things)) {
        val pointsToMove = getPointsToMove(point, direction, things)
//        println("pointsToMove: ${pointsToMove}")
        pointsToMove.sortReversed(direction).forEach { point ->
            val newPoint = point + direction
            things[newPoint] = things[point]!!
            things.remove(point)
        }
        true
    } else {
        false
    }
}

fun Collection<Vector>.sortReversed(direction: Vector) =
    when (direction) {
        Vector(0, 1) -> {
            sortedByDescending { it.y }
        }
        Vector(0, -1) -> {
            sortedBy { it.y }
        }
        Vector(1, 0) -> {
            sortedByDescending { it.x }
        }
        Vector(-1, 0) -> {
            sortedBy { it.x }
        }
        else ->{
            throw Error("9ew740983247")
        }
    }

fun canMove(point: Vector, direction: Vector, grid: Grid<String>, things: Map<Vector, String>): Boolean {
//    println("point: ${point}")
    val destinations = getDestinations(point, direction, things)
//    println("destinations: $destinations")
//    println(destinations.map { it to whatsThere(it, grid, things) })
    val whatsThere = destinations.map { whatsThere(it, grid, things) }
    return when {
        whatsThere.any { it == "#" } -> false
        whatsThere.all { it == "." } -> true
        else -> destinations.all { canMove(it, direction, grid, things) }
    }
}

fun getPointsToMove(point: Vector, direction: Vector, things: Map<Vector, String>): Set<Vector> {
    val pointsToMove = mutableSetOf<Vector>()

    fun go(point: Vector) {
        val movementIsHorizontal = direction == E.vector || direction == W.vector
        if (movementIsHorizontal) {
            pointsToMove += point
            val pushedPoint = point + direction
            if (pushedPoint in things) {
                go(pushedPoint)
            }
        } else {
//            println("point: $point")
            val thing = things[point]
            val pointAndNeighbour =
                if (thing == "@") listOf(point)
                else {
                    if (thing == "[") listOf(point, point + E)
                    else listOf(point, point + W)
                }
//            println("pointAndNeighbour: $pointAndNeighbour")
            pointsToMove += pointAndNeighbour
            val pushedPoints = pointAndNeighbour.map { it + direction }
            val occupiedPushedPoints = pushedPoints.filter { it in things }
            occupiedPushedPoints.forEach(::go)
        }
    }

    go(point)

    return pointsToMove
}

fun getDestinations(point: Vector, direction: Vector, things: Map<Vector, String>): List<Vector> {
    val thing = things[point]
    val movementIsHorizontal = direction == E.vector || direction == W.vector
    val destinations = if (thing == "@") {
        listOf(point + direction)
    } else {
        if (movementIsHorizontal) {
            listOf(point + direction)
        } else {
            val neighbour = if (thing == "[") point + E.vector else point + W.vector
            listOf(point + direction, neighbour + direction)
        }
    }
    return destinations
}

fun whatsThere(point: Vector, grid: Grid<String>, things: Map<Vector, String>): String {
    return things[point] ?: grid.at(point, ".")
}

fun Map<Vector, String>.score() =
    toList().filter { it.second == "O" }.sumOf { (v) -> v.x + (100 * v.y) }

fun Map<Vector, String>.score2() =
    toList().filter { it.second == "[" }.sumOf { (v) -> v.x + (100 * v.y) }

fun Grid<String>.printy(things: Map<Vector, String>) {
    forEachIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val point = Vector(x, y)
            when (point) {
                in things -> things[point]
                else -> cell
            }
        }
            .joinToString("")
            .also(::println)
    }
}

fun parseInput2(input: String): Input {
    val input1 = parseInput(input)
    return input1.copy(
        grid = input1.grid.map { row ->
            row.flatMap { cell ->
                if (cell == "#") listOf("#", "#") else listOf(".", ".")
            }
        },
        things = input1.things.entries.flatMap { (point, thing) ->
            when (thing) {
                "O" -> listOf(Vector(point.x * 2, point.y) to "[", Vector(point.x * 2 + 1, point.y) to "]")
                "@" -> listOf(Vector(point.x * 2, point.y) to "@")
                else -> throw Error("Unknown thing?!")
            }
        }.toMap().toMutableMap()
    )
}

fun parseInput(input: String): Input {
    val (first, second) = input.split("\n\n")
    val grid = first.split("\n").map { row ->
        row.split("").filter { cell -> cell.isNotBlank() }
    }

    val robot = grid.cells().find { (_, value) -> value == "@" }?.first!!
    val boxes = grid.cells().filter { (_, value) -> value == "O" }.map { it.first }.toSet()

    val things = mutableMapOf(robot to "@")
    boxes.forEach { things[it] = "O" }

    return Input(
        grid = grid.map { row ->
            row.map { cell ->
                if (cell == "#" || cell == ".") cell else "."
            }
        },
        things = things,
        moves = second.split("").mapNotNull {
            when (it) {
                "<" -> W
                "^" -> CompassDirection.N
                ">" -> E
                "v" -> CompassDirection.S
                else -> null
            }
        }
    )
}

data class Input(
    val grid: Grid<String>,
    val things: MutableMap<Vector, String>,
    val moves: List<CompassDirection>
)
