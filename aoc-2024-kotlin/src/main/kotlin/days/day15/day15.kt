package days.day15

import kotlin.system.exitProcess
import lib.CompassDirection
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

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(1415498)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String): Int {
    val input = parseInput(input)
    var robot = input.robot
    val things = mutableMapOf(robot to "@")
    input.boxes.forEach { things[it] = "O" }

    input.moves.forEach { move ->
        if (move(robot, move.vector, input.grid, things)) {
            robot += move.vector
        }
    }

    return things.score()
}

fun part2(input: String): Int {
    return -1
}

fun move(point: Vector, direction: Vector, grid: Grid<String>, things: MutableMap<Vector, String>): Boolean {
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
            if (move(newPoint, direction, grid, things)) {
                things[newPoint] = things[point]!!
                things.remove(point)
                return true
            } else {
                return false
            }
        }
    }
}

fun Map<Vector, String>.score() = toList().filter { it.second == "O" }.sumOf { (v) -> v.x + (100 * v.y) }

fun Grid<String>.printy(things: Map<Vector, String>) {
    println()
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

fun parseInput(input: String): Input {
    val (first, second) = input.split("\n\n")
    val grid = first.split("\n").map { it.split("").filter { it.isNotBlank() } }
    return Input(
        grid = grid.map { row ->
            row.map { cell ->
                if (cell == "#" || cell == ".") cell else "."
            }
        },
        robot = grid.cells().find { (_, value) -> value == "@" }?.first!!,
        boxes = grid.cells().filter { (_, value) -> value == "O" }.map { it.first }.toSet(),
        moves = second.split("").mapNotNull {
            when (it) {
                "<" -> CompassDirection.W
                "^" -> CompassDirection.N
                ">" -> CompassDirection.E
                "v" -> CompassDirection.S
                else -> null
            }
        }
    )
}

data class Input(
    val grid: Grid<String>,
    val robot: Vector,
    val boxes: Set<Vector>,
    val moves: List<CompassDirection>
)
