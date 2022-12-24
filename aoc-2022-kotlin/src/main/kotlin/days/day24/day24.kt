package days.day24

import lib.Reader
import lib.Vector
import java.util.PriorityQueue

fun main() {
    val input = Reader("day24.txt").strings()
    val exampleInput = Reader("day24-example.txt").strings()
    val simpleExampleInput = Reader("day24-simple-example.txt").strings()

    part1(input)
    // 337 is too high (also 336)
}

fun part1(input: List<String>) {
    val initialBoard = parse(input)
    val boardPermutations = generateBoardPermutations(initialBoard)
    val initialState = State(position = Vector(1, 0), boardIndex = 0)
    aStar(initialState, boardPermutations)
        .also { println(it.size) }
}

fun generateBoardPermutations(initialBoard: Board): List<Board> {
    val boardPermutations = mutableListOf(initialBoard)

    tailrec fun go(board: Board) {
        val newBoard = board.updateBlizzards()
        if (newBoard == boardPermutations.first()) return
        else {
            boardPermutations.add(newBoard)
            go(newBoard)
        }
    }

    go(initialBoard)

    return boardPermutations
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

fun aStar(initial: State, boards: List<Board>): List<State> {
    val (start, goal) = boards.first().let { Pair(it.initial, it.destination) }
    fun h(state: State) = (goal - state.position).manhattanDistance
    val openSet2 = PriorityQueue(compareBy<State> { -h(it) })
    openSet2.add(initial)
    val cameFrom = mutableMapOf<State, State>()
    val gScore = mutableMapOf(initial to Int.MAX_VALUE)
    val fScore = mutableMapOf(initial to h(initial))
    while (openSet2.isNotEmpty()) {
        val current = openSet2.poll()
        if (current.position == goal) {
            return reconstructPath(cameFrom, start, current)
        } else {
            current.possibleNextStates(boards).forEach { neighbour ->
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
    val boardIndex: Int
) {
    fun possibleNextStates(boards: List<Board>): List<State> {
        val newBoardIndex = (boardIndex + 1) % boards.size
        val newBoard = boards[newBoardIndex]
        val newPositions = Moves.values().map { position + it.v }.filter {
            it !in newBoard.blizzards.keys && newBoard.isWithinBounds(it)
        }
        return newPositions.map { newPosition ->
            State(position = newPosition, boardIndex = newBoardIndex)
        }
    }
}

data class Board(
    val blizzards: Map<Vector, List<Vector>>,
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
) {
    val initial = Vector(1, 0)
    val destination = Vector(maxX - 1, maxY)

    fun updateBlizzards(): Board {
        val newBlizzards = this.blizzards.flatMap { (location, directions) ->
            directions.map { direction ->
                updateOneBlizzard(location, direction) to direction
            }
        }.groupBy({ it.first }) { it.second }
        return copy(blizzards = newBlizzards)
    }

    fun isWithinBounds(position: Vector): Boolean =
        position == destination || position == initial || (
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
