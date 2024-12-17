package days.day16

import lib.CompassDirection
import lib.Grid
import lib.Reader
import lib.Vector
import lib.atOrNull
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day16/input.txt").grid()
    val exampleInput = Reader("/day16/example-1.txt").grid()

    time(message = "Part 1", warmUp = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(92432)

    time(message = "Part 2", warmUp = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(458)
}

fun part1(inputString: Grid<Char>): Int {
    val input = parseInput(inputString)
    val (_, start, end) = input
    val nodes = getNodes(input)

    val initialNode = State(start, CompassDirection.E)

    fun h(state: State) =
        (end - state.point).manhattanDistance

    val openSet = mutableSetOf(initialNode)
    val cameFrom = mutableMapOf<State, State>()
    val gScore = mutableMapOf(initialNode to 0)
    val fScore = mutableMapOf(initialNode to h(initialNode))

    var current = initialNode

    while (openSet.isNotEmpty()) {
        current = openSet.minBy { fScore[it]!! }
        if (current.point == end) {
            break
        }
        openSet.remove(current)
        nodes[current]!!.forEach { (neighbour, cost) ->
            val tentativeGScore = gScore[current]!! + cost
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

    return gScore[current]!!
}

fun part2(inputString: Grid<Char>): Int {
    val input = parseInput(inputString)
    val (_, start, end) = input
    val nodes = getNodes(input)

    val dist = mutableMapOf<State, Int>()
    val prev = mutableMapOf<State, List<State>>()
    val q = mutableSetOf<State>()
    nodes.entries.forEach { (node, _) ->
        dist[node] = Int.MAX_VALUE
        prev[node] = emptyList()
        q.add(node)
    }
    val initialNode = State(start, CompassDirection.E)
    dist[initialNode] = 0

    while (q.isNotEmpty()) {
        val u = q.minBy { dist[it]!! }
        q.remove(u)
        nodes[u]!!.forEach { (neighbour, cost) ->
            val alt = dist[u]!! + cost
            when {
                alt == dist[neighbour]!! -> {
                    dist[neighbour] = alt
                    prev[neighbour] = prev[neighbour]!! + u
                }

                alt < dist[neighbour]!! -> {
                    dist[neighbour] = alt
                    prev[neighbour] = listOf(u)
                }
            }
        }
    }

    fun getPaths(path: List<State>): List<List<State>> =
        if (path[0] == initialNode) {
            listOf(path)
        } else {
            val us = prev[path[0]]!!
            us.map { listOf(it) + path }.flatMap(::getPaths)
        }

    val paths = getPaths(CompassDirection.entries.map { State(end, it) })

    return paths.flatMap { path -> pathToPoints(path) }.toSet().size
}

fun pathToPoints(path: List<State>) =
    path.map(State::point).windowed(2).flatMap { (first, second) -> first.pathTo(second) }.toSet()

fun getNodes(input: Input): Map<State, Map<State, Int>> {
    val nodes = input.grid.cells().mapNotNull { (point, cell) ->
        when {
            cell == '#' -> null
            else -> {
                val nonWallNeighbours =
                    CompassDirection.entries.filter { input.grid.atOrNull(point + it) == '.' }

                when {
                    nonWallNeighbours.size == 2 && nonWallNeighbours[0] == nonWallNeighbours[1].opposite() -> null
                    else -> point
                }
            }
        }
    }

    return nodes.flatMap { node ->
        CompassDirection.entries.map { facing -> State(node, facing) }
    }.associateWith { node ->
        val turnLeft = Pair(node.copy(facing = node.facing.rotateLeft()), 1000)
        val turnRight = Pair(node.copy(facing = node.facing.rotateRight()), 1000)

        if (input.grid.atOrNull(node.point + node.facing) == '.') {
            var nextPoint = node.point + node.facing
            var distance = 1
            while (nextPoint !in nodes) {
                nextPoint += node.facing
                distance += 1
            }
            val onward = Pair(node.copy(point = nextPoint), distance)

            listOf(turnLeft, turnRight, onward).toMap()
        } else {
            listOf(turnLeft, turnRight).toMap()
        }
    }
}

fun parseInput(input: Grid<Char>): Input {
    return Input(
        grid = input.map { row -> row.map { if (it == 'S' || it == 'E') '.' else it } },
        start = input.cells().single { it.second == 'S' }.first,
        end = input.cells().single { it.second == 'E' }.first,
    )
}

data class Input(val grid: Grid<Char>, val start: Vector, val end: Vector)

data class State(val point: Vector, val facing: CompassDirection)
