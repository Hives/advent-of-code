package days.day24

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.util.PriorityQueue

fun main() {
    val input = Reader("day24.txt").strings()
    val exampleInput = Reader("day24-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 10, iterations = 5) {
        part1(input)
    }.checkAnswer(262)

    time(message = "Part 2", warmUpIterations = 8, iterations = 3) {
        part2(input)
    }.checkAnswer(785)
}

fun part1(input: List<String>): Int {
    val valley = parse(input)
    val initialState = State(valley.start, 0)
    val final = aStar(initialState, valley, valley.goal)
    return final.time
}

fun part2(input: List<String>): Int {
    val valley = parse(input)
    val atTheStart = State(valley.start, 0)
    val atTheEnd = aStar(atTheStart, valley, valley.goal)
    val atTheStartAgain = aStar(atTheEnd, valley, valley.start)
    val atTheEndAgain = aStar(atTheStartAgain, valley, valley.goal)
    return atTheEndAgain.time
}

fun aStar(initialState: State, valley: Valley, goal: Vector): State {
    val cameFrom = mutableMapOf<State, State>()
    val costSoFar = mutableMapOf(initialState to initialState.time)
    fun estimatedCostToGoal(state: State) = (valley.goal - state.location).manhattanDistance
    val frontier = PriorityQueue(compareBy<State> { costSoFar[it]!! + estimatedCostToGoal(it) })
    frontier.add(initialState)

    while (frontier.isNotEmpty()) {
        val current = frontier.poll()

        if (current.location == goal) return current

        valley.nextStates(current).forEach { next ->
            val newCost = costSoFar[current]!! + 1
            if (newCost < costSoFar.getOrDefault(next, INFINITY)) {
                costSoFar[next] = newCost
                frontier.add(next)
                cameFrom[next] = current
            }
        }
    }

    throw Exception("Frontier was empty before goal was reached")
}

data class State(
    val location: Vector,
    val time: Int
)

typealias Snow = Map<Vector, List<Vector>>

class Valley(
    initialSnow: Snow,
    private val minX: Int,
    private val maxX: Int,
    private val minY: Int,
    private val maxY: Int,
) {
    val start = Vector(1, 0)
    val goal = Vector(maxX - 1, maxY)

    fun nextStates(state: State): List<State> {
        return moves.map { state.location + it }
            .filter { it.isWithinBounds() }
            .filter { it !in getSnow(state.time + 1).keys }
            .map { State(it, state.time + 1) }
    }

    private fun getSnow(time: Int): Snow =
        if (time in cachedSnow) cachedSnow[time]!!
        else {
            val nextSnow = getNextSnow(getSnow(time - 1))
            cachedSnow[time] = nextSnow
            nextSnow
        }

    private fun getNextSnow(snow: Snow): Snow =
        snow.flatMap { (location, directions) ->
            directions.map { direction ->
                moveOneSnow(location, direction) to direction
            }
        }.groupBy({ it.first }, { it.second })

    private fun moveOneSnow(location: Vector, direction: Vector): Vector {
        val moved = location + direction
        return when {
            direction == Vector(0, 1) && moved.y == maxY -> Vector(moved.x, minY + 1)
            direction == Vector(0, -1) && moved.y == minY -> Vector(moved.x, maxY - 1)
            direction == Vector(1, 0) && moved.x == maxX -> Vector(minX + 1, moved.y)
            direction == Vector(-1, 0) && moved.x == minX -> Vector(maxX - 1, moved.y)
            else -> moved
        }
    }

    private fun Vector.isWithinBounds() =
        this == start ||
                this == goal ||
                (this.x in (minX + 1) until maxX && this.y in (minY + 1) until maxY)

    private val moves = listOf(Vector(0, 1), Vector(0, -1), Vector(1, 0), Vector(-1, 0), Vector(0, 0))
    private val cachedSnow = mutableMapOf(0 to initialSnow)
}

fun parse(input: List<String>): Valley {
    val snow = input.indices.flatMap { y ->
        input[y].indices.mapNotNull { x ->
            when (input[y][x]) {
                '>' -> Vector(x, y) to listOf(Vector(1, 0))
                'v' -> Vector(x, y) to listOf(Vector(0, 1))
                '<' -> Vector(x, y) to listOf(Vector(-1, 0))
                '^' -> Vector(x, y) to listOf(Vector(0, -1))
                else -> null
            }
        }
    }.toMap()

    return Valley(
        initialSnow = snow,
        minX = 0,
        maxX = input.first().length - 1,
        minY = 0,
        maxY = input.size - 1,
    )
}

const val INFINITY = 1_000_000
