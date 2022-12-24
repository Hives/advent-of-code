package days.day24

import lib.Reader
import lib.Vector
import java.util.PriorityQueue

fun main() {
    val input = Reader("day24.txt").strings()
    val exampleInput = Reader("day24-example.txt").strings()
    val simpleExampleInput = Reader("day24-simple-example.txt").strings()

    part1(input)
}

fun part1(input: List<String>) {
    val initial = parse(input)

    aStar(initial)
        .also { println(it.size) }
}

fun reconstructPath(cameFrom: Map<State, State>, start: Vector, goal: State): List<State> {
    println("reconstructin")
    val path = mutableListOf(goal)
    var current = goal
    while (current.position != start) {
        println(current.position)
        current = cameFrom[current]!!
        path.add(current)
    }
    return path
}

fun aStar(initial: State): List<State> {
    val goal = initial.destination
    fun h(state: State) = (goal - state.position).manhattanDistance
    val openSet2 = PriorityQueue(compareBy<State> { -h(it) })
    openSet2.add(initial)
//    val openSet = mutableSetOf(initial)
    val cameFrom = mutableMapOf<State, State>()
    val gScore = mutableMapOf(initial to Int.MAX_VALUE)
    val fScore = mutableMapOf(initial to h(initial))
    while (openSet2.isNotEmpty()) {
        val current = openSet2.poll()
//        println(current.position)
        if (current.position == goal) {
            return reconstructPath(cameFrom, initial.initial, current)
        } else {
            current.possibleNextStates().forEach { neighbour ->
                val tentativeGScore = gScore[current]!! + 1
                if (tentativeGScore < gScore.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    cameFrom[neighbour] = current
                    gScore[neighbour] = tentativeGScore
                    fScore[neighbour] = tentativeGScore + h(neighbour)
                    if (neighbour !in openSet2) {
                        openSet2.add(neighbour)
                    }
                }
            }
        }
    }
    throw Exception("open set is empty but goal was not reached")
}

data class State(
    val position: Vector,
    val blizzards: Map<Vector, List<Vector>>,
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
) {
    val initial = Vector(1, 0)
    val destination = Vector(maxX - 1, maxY)

    fun possibleNextStates(): List<State> {
        val newBlizzards = updateBlizzards()
        val newPositions = Moves.values().map { position + it.v }.filter {
            it !in newBlizzards.keys && isWithinBounds(it)
        }
        return newPositions.map { this.copy(position = it, blizzards = newBlizzards) }
    }

    fun isWithinBounds(position: Vector): Boolean =
        position == destination || position == initial || (
                (position.x in (minX + 1) until maxX) &&
                        (position.y in (minY + 1) until maxY)
                )

    fun updateBlizzards() =
        this.blizzards.flatMap { (location, directions) ->
            directions.map { direction ->
                updateOneBlizzard(location, direction) to direction
            }
        }.groupBy({ it.first }) { it.second }

    fun printy() {
        println()
        (minY..maxY).map { y ->
            when (y) {
                minY -> List(maxX - minX + 1) { '#' }.joinToString("")
                maxY -> List(maxX - minX + 1) { '#' }.joinToString("")
                else -> (minX..maxX).joinToString("") { x ->
                    val v = Vector(x, y)
                    when {
                        v == position -> "E"
                        x == minX -> "#"
                        x == maxX -> "#"
                        v in blizzards.keys -> {
                            if (blizzards[v]!!.size > 1) blizzards[v]!!.size.toString()
                            else when (blizzards[v]!!.single()) {
                                Moves.UP.v -> "^"
                                Moves.DOWN.v -> "v"
                                Moves.LEFT.v -> "<"
                                Moves.RIGHT.v -> ">"
                                else -> throw Exception("Bad blizzard direction")
                            }
                        }

                        else -> "."
                    }
                }
            }
        }.forEach { println(it) }
    }

    private fun updateOneBlizzard(location: Vector, direction: Vector): Vector {
        val moved = location + direction
        return if (direction == Moves.UP.v || direction == Moves.DOWN.v) {
            when (moved.y) {
                minY -> Vector(moved.x, maxY - 1)
                maxY -> Vector(moved.x, minY + 1)
                else -> moved
            }
        } else {
            when (moved.x) {
                minX -> Vector(maxX - 1, moved.y)
                maxX -> Vector(minX + 1, moved.y)
                else -> moved
            }
        }
    }

}

fun parse(input: List<String>): State {
    val blizzards = input.indices.flatMap { y ->
        input[y].indices.mapNotNull { x ->
            when (input[y][x]) {
                '>' -> Vector(x, y) to listOf(Moves.RIGHT.v)
                'v' -> Vector(x, y) to listOf(Moves.DOWN.v)
                '<' -> Vector(x, y) to listOf(Moves.LEFT.v)
                '^' -> Vector(x, y) to listOf(Moves.UP.v)
                else -> null
            }
        }
    }.toMap()

    return State(
        position = Vector(1, 0),
        blizzards = blizzards,
        minX = 0,
        maxX = input.first().length - 1,
        minY = 0,
        maxY = input.size - 1,
    )
}


enum class Moves(val v: Vector) {
    UP(Vector(0, -1)),
    RIGHT(Vector(1, 0)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    WAIT(Vector(0, 0))
}
