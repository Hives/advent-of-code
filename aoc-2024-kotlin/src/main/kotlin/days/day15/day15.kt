package days.day15

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

    time(message = "Part 1", warmUp = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(1415498)

    time(message = "Part 2", warmUp = 5, iterations = 5) {
        part2(input)
    }.checkAnswer(1432898)
}

fun part1(input: String) =
    parseInput(input).let(::doIt)

fun part2(input: String) =
    parseInput(input).expand().let(::doIt)

fun doIt(input: Input): Int {
    val (grid, things, moves) = input

    moves.forEach { move ->
        val pointsThatMove = getPointsThatMove(move.vector, grid, things)
        if (pointsThatMove != null) {
            movePoints(pointsThatMove, move.vector, things)
        }
    }

    return things.score()
}

fun getPointsThatMove(
    direction: Vector,
    grid: Grid<String>,
    things: MutableMap<Vector, String>
): Set<Vector>? {
    fun go(
        point: Vector,
        pointsThatMove: Set<Vector>
    ): Set<Vector>? {
        val destinationPoint = point + direction
        val destinationThing = whatsThere(destinationPoint, grid, things)
        return when (destinationThing) {
            "." -> pointsThatMove + point
            "#" -> null
            "O" -> go(destinationPoint, pointsThatMove + point)
            "[", "]" -> {
                val motionIsHorizontal = direction == E.vector || direction == W.vector

                if (motionIsHorizontal) {
                    go(destinationPoint, pointsThatMove + point)
                } else {
                    val box = if (destinationThing == "[") {
                        Pair(destinationPoint, destinationPoint + E.vector)
                    } else {
                        Pair(destinationPoint + W.vector, destinationPoint)
                    }
                    val points1 = go(box.first, pointsThatMove + point)
                    val points2 = go(box.second, pointsThatMove + point)
                    if (points1 == null || points2 == null) null
                    else points1 + points2
                }
            }

            else -> throw Error("Invalid type of thing: $destinationThing")
        }
    }

    return go(things.getRobot(), emptySet())
}

fun movePoints(
    points: Set<Vector>,
    direction: Vector,
    things: MutableMap<Vector, String>
) {
    val newPoints = points.map { point -> point + direction to things[point]!! }
    points.forEach { things.remove(it) }
    newPoints.forEach { things[it.first] = it.second }
}

fun Map<Vector, String>.getRobot() =
    entries.single { it.value == "@" }.key

fun whatsThere(point: Vector, grid: Grid<String>, things: Map<Vector, String>): String {
    return things[point] ?: grid.at(point, ".")
}

fun Map<Vector, String>.score() =
    toList()
        .filter { it.second == "O" || it.second == "[" }
        .sumOf { (v) -> v.x + (100 * v.y) }

fun printy(grid: Grid<String>, things: Map<Vector, String>) {
    grid.forEachIndexed { y, row ->
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
    val grid = first.split("\n").map { row ->
        row.split("").filter { cell -> cell.isNotBlank() }
    }

    val things = mutableMapOf<Vector, String>()
    grid.cells()
        .filter { (_, value) -> value == "@" || value == "O" }
        .forEach { (point, thing) ->
            things[point] = thing
        }

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
) {
    fun expand(): Input {
        return copy(
            grid = grid.map { row ->
                row.flatMap { cell ->
                    if (cell == "#") listOf("#", "#") else listOf(".", ".")
                }
            },
            things = things.entries.flatMap { (point, thing) ->
                when (thing) {
                    "O" -> listOf(Vector(point.x * 2, point.y) to "[", Vector(point.x * 2 + 1, point.y) to "]")
                    "@" -> listOf(Vector(point.x * 2, point.y) to "@")
                    else -> throw Error("Invalid thing: $thing")
                }
            }.toMap().toMutableMap()
        )
    }
}
