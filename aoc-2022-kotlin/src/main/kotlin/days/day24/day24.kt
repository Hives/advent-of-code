package days.day24

import lib.Reader
import lib.Vector
import java.util.PriorityQueue
import kotlin.system.exitProcess

fun main() {
    val input = Reader("day24.txt").strings()
    // not 251
    // not 252
    // not 253
    // not 279
    // 285 is too high
    // 337 is too high (also 336)

    val exampleInput = Reader("day24-example.txt").strings()
    val simpleExampleInput = Reader("day24-simple-example.txt").strings()

    part1(input)
}

fun part1(input: List<String>) {
    val board = parse(input)
    val start = board.start
    val goal = board.goal

    val initialState = State(position = start, time = 0)
    val path = aStar(initialState, board)

    println(path.map { it.position })
    val stepValidation = path.windowed(2, 1).all {
        val from = it[0]
        val to = it[1]
        (from.position - to.position).manhattanDistance <= 1
    }
    println("step validation: $stepValidation")
    println("start validation: ${path.first().position == start}")
    println("end validation: ${path.last().position == goal}")
    val blizzardValidation = path.all {
        it.position !in board.getBlizzard(it.time)
    }
    println("blizzard validation: $blizzardValidation")

    println("time taken: ${path.size - 1}")
}

fun aStar(initial: State, board: Board): List<State> {
    fun heuristic(state: State) = (state.position - board.goal).manhattanDistance

    val cameFrom = mutableMapOf<State, State>()

    val cheapestKnownCostsToNodes = mutableMapOf(initial to 0)

    val estimatedCostToGoal = mutableMapOf(initial to heuristic(initial))

    val openSet = PriorityQueue(compareBy<State> { estimatedCostToGoal[it] })
    openSet.add(initial)

    var counter = 0

    while (openSet.isNotEmpty()) {
        counter++;
        if (counter == 100000) exitProcess(0)
//        println()
//        println("openSet:")
//        openSet.forEach { println("$it - estimated cost to goal ${estimatedCostToGoal[it]}") }
        val current = openSet.poll()

//        println("current: $current - score: ${cheapestKnownCostsToNodes[current]}")
//        board.printy(current.position, current.time)

        if (current.position == board.goal) {
            return reconstructPath(cameFrom, board.start, current)
        } else {
            current.possibleNextStates(board).also {
//                println("possible next locations: ${it.map { it.position }}")
            }.forEach { nextState ->
//                println()
//                println("nextState: $nextState")
                val tentativeGScore = cheapestKnownCostsToNodes[current]!! + 1
//                println("tentativeGScore: $tentativeGScore")
//                println("cheapest known cost to that node: ${cheapestKnownCostsToNodes.getOrDefault(nextState, INFINITY)}")
                if (tentativeGScore < cheapestKnownCostsToNodes.getOrDefault(nextState, INFINITY)) {
//                    println("havin that")
                    cameFrom[nextState] = current
                    cheapestKnownCostsToNodes[nextState] = tentativeGScore
                    estimatedCostToGoal[nextState] = tentativeGScore + heuristic(nextState)
//                    println("estimated cost to goal: ${estimatedCostToGoal[nextState]}")
                    openSet.add(nextState)
                }
            }
        }
    }
    throw Exception("open set is empty but goal was not reached")
}

fun reconstructPath(cameFrom: Map<State, State>, start: Vector, goal: State): List<State> {
    val path = mutableListOf(goal)
    var current = goal
    while (current.position != start) {
        println(current.position)
        current = cameFrom[current]!!
        path.add(current)
    }
    return path.reversed()
}

data class State(
    val position: Vector,
    val time: Int
) {
    fun possibleNextStates(board: Board): List<State> {
        val nextBlizzard = board.getBlizzard(time + 1)
        val newPositions = Moves.values().map { position + it.v }.filter {
            it !in nextBlizzard.keys && board.isWithinBounds(it)
        }
        return newPositions.map { newPosition ->
            State(
                position = newPosition,
                time = time + 1
            )
        }
    }

}

typealias Blizzard = Map<Vector, List<Vector>>

class Board(
    initialBlizzard: Blizzard,
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
) {
    val start = Vector(1, 0)
    val goal = Vector(maxX - 1, maxY)
    private val blizzards = generateBoardPermutations(initialBlizzard)

    fun getBlizzard(time: Int) = blizzards[time % blizzards.size]

    fun printy(position: Vector, time: Int) {
        println()
        val blizzard = getBlizzard(time)
        val grid = (0..maxY).map { y ->
            (0..maxX).map { x ->
                when {
                    Vector(x, y) == position -> "E"
                    Vector(x, y) == start -> "."
                    Vector(x, y) == goal -> "."
                    y == minY || y == maxY || x == minX || x == maxX -> "#"
                    else -> "."
                }
            }.toMutableList()
        }

        blizzard.forEach { (location, directions) ->
            val c = if (directions.size > 1) "${directions.size}"
            else {
                when (directions.single()) {
                    Moves.UP.v -> "^"
                    Moves.DOWN.v -> "v"
                    Moves.LEFT.v -> "<"
                    Moves.RIGHT.v -> ">"
                    else -> throw Exception("Bad direction ${directions.single()}")
                }
            }
            grid[location.y][location.x] = c
        }

        grid.forEach { println(it.joinToString("")) }
    }

    private fun generateBoardPermutations(initialBlizzard: Blizzard): List<Blizzard> {
        val blizzards = mutableListOf(initialBlizzard)

        tailrec fun go(blizzard: Blizzard) {
            val newBlizzard = nextBlizzard(blizzard)
            if (newBlizzard == blizzards.first()) return
            else {
                blizzards.add(newBlizzard)
                go(newBlizzard)
            }
        }

        go(initialBlizzard)

        return blizzards
    }


    private fun nextBlizzard(blizzard: Blizzard) =
        blizzard.flatMap { (location, directions) ->
            directions.map { direction ->
                updateOneBlizzard(location, direction) to direction
            }
        }.groupBy({ it.first }) { it.second }

    fun isWithinBounds(position: Vector): Boolean =
        position == goal || position == start || (
                (position.x in (minX + 1) until maxX) &&
                        (position.y in (minY + 1) until maxY)
                )

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

fun parse(input: List<String>): Board {
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

    return Board(
        initialBlizzard = blizzards,
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
    WAIT(Vector(0, 0)),
}

const val INFINITY = 1_000_000
