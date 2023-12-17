package days.day17

import days.day17.Direction.Down
import days.day17.Direction.Left
import days.day17.Direction.Right
import days.day17.Direction.Up
import lib.IntGrid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.util.PriorityQueue

fun main() {
    val input = Reader("/day17/input.txt").intGrid()
    val exampleInput = Reader("/day17/example-1.txt").intGrid()

    time(message = "Part 1", warmUpIterations = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(1238)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(1362)
}

fun part1(input: IntGrid) =
    aStar(input, StandardCrucibleBits)

fun part2(input: IntGrid) =
    aStar(input, UltraCrucibleBits)

fun aStar(grid: IntGrid, bits: Bits): Int? {
    val start = Node(Vector(0, 0), 0, Right)
    val goal = Vector(grid.last().size - 1, grid.size - 1)

    fun heuristic(node: Node) = (goal - node.location).manhattanDistance

//    val cameFrom = mutableMapOf<Node, Node>()
    val gScore = mutableMapOf(start to 0)
    val fScore = mutableMapOf(start to heuristic(start))

    val openSet = PriorityQueue { a: Node, b: Node ->
        fScore.getOrDefault(a, Int.MAX_VALUE).compareTo(fScore.getOrDefault(b, Int.MAX_VALUE))
    }
    openSet.add(start)

    var count = 0L

    while (openSet.isNotEmpty()) {
        count++
        val current = openSet.poll()
        if (bits.isGoal(current, grid)) {
            println("gScoreMin: ${gScore.values.min()}")
            println("gScoreMax: ${gScore.values.max()}")
            println("count: $count")
            return gScore[current]
        }

        val possibleNext = bits.getViableNextNodes(current, grid)
        possibleNext.forEach { next ->
            val tentativeGScore = gScore[current]!! + grid.at(next.location)!!
            if (tentativeGScore < (gScore.getOrDefault(next, Int.MAX_VALUE))) {
//                cameFrom[next] = current
                gScore[next] = tentativeGScore
                fScore[next] = tentativeGScore + heuristic(next)
                if (next !in openSet) openSet.add(next)
            }
        }
    }

    throw Error("Oh no")
}

object StandardCrucibleBits : Bits {
    override fun getViableNextNodes(node: Node, grid: IntGrid): List<Node> {
        val (turns, straightOn) = node.getNextNodes()

        return when {
            node.straightLineCount >= 3 -> turns
            else -> turns + straightOn
        }.filterNot { grid.at(it.location) == null }
    }

    override fun isGoal(node: Node, grid: IntGrid): Boolean =
        node.location.y == grid.size - 1 && node.location.x == grid.last().size - 1
}

object UltraCrucibleBits : Bits {
    override fun getViableNextNodes(node: Node, grid: IntGrid): List<Node> {
        val (turns, straightOn) = node.getNextNodes()

        return when {
            node.straightLineCount == 0 -> turns + straightOn
            node.straightLineCount < 4 -> listOf(straightOn)
            node.straightLineCount >= 10 -> turns
            else -> turns + straightOn
        }.filterNot { grid.at(it.location) == null }
    }

    override fun isGoal(node: Node, grid: IntGrid): Boolean =
        node.straightLineCount >= 4 &&
                (node.location.y == grid.size - 1 && node.location.x == grid.last().size - 1)
}

fun Node.getNextNodes(): Pair<List<Node>, Node> {
    val turns = when (previousDirection) {
        Left -> listOf(Up, Down)
        Right -> listOf(Up, Down)
        Up -> listOf(Left, Right)
        Down -> listOf(Left, Right)
    }.map { d ->
        Node(location + d, 1, d)
    }
    val straightOn = Node(
        location + previousDirection, straightLineCount + 1,
        previousDirection
    )
    return Pair(turns, straightOn)
}

interface Bits {
    fun getViableNextNodes(node: Node, grid: IntGrid): List<Node>
    fun isGoal(node: Node, grid: IntGrid): Boolean
}

data class Node(val location: Vector, val straightLineCount: Int, val previousDirection: Direction)

operator fun Vector.plus(other: Direction) = Vector(
    x = this.x + other.v.x,
    y = this.y + other.v.y,
)

sealed class Direction(val v: Vector) {
    object Left : Direction(Vector(-1, 0))
    object Right : Direction(Vector(1, 0))
    object Up : Direction(Vector(0, -1))
    object Down : Direction(Vector(0, 1))
}

fun IntGrid.at(v: Vector) =
    if (v.y < 0 || v.y >= this.size || v.x < 0 || v.x >= this[v.y].size) null
    else this[v.y][v.x]
