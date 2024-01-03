package days.day23

import lib.CompassDirection
import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day23/input.txt").grid()
    val exampleInput = Reader("/day23/example-1.txt").grid()
    val exampleInput2 = Reader("/day23/example-2.txt").grid()


    time(message = "Part 1", warmUpIterations = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(2218)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(6674)
}

fun part1(map: Grid): Int {
    val start = Vector(1, 0)
    val end = Vector(map.first().size - 2, map.size - 1)

    val paths = mutableListOf<Pair<Set<Vector>, Vector>>()
    paths.add(Pair(emptySet(), start))

    var longestPath = 0;

    while (paths.isNotEmpty()) {
        val (history, current) = paths.removeLast()
        if (current == end) {
            if (history.size > longestPath) longestPath = history.size
        } else {
            val next =
                when (map.at(current)) {
                    '.' -> {
                        CompassDirection.values().map { current + it }.filterNot { map.at(it) == '#' }
                    }

                    '>' -> listOf(current + CompassDirection.E)
                    '<' -> listOf(current + CompassDirection.W)
                    '^' -> listOf(current + CompassDirection.S) // !!
                    'v' -> listOf(current + CompassDirection.N) // !!
                    else -> throw Exception("?!")
                }
            next.filterNot { history.contains(it) }.forEach {
                paths.add(Pair(history + current, it))
            }
        }
    }

    return longestPath
}

fun part2(map: Grid): Int {
    val start = Vector(1, 0)
    val end = Vector(map.first().size - 2, map.size - 1)

    val paths = mutableListOf(listOf(start))
    val edges = mutableSetOf<Triple<Vector, Vector, Int>>()
    val nodes = mutableSetOf(start)

    while (paths.isNotEmpty()) {
        val path = paths.removeLast()
        val next = CompassDirection.values()
            .map { path.last() + it }
            .filter { map.at(it) != '#' }
            .filter { it !in path }
        next.forEach {
            when {
                it == end -> {
                    val a = path.first()
                    val b = it
                    val segment = if (a > b) Pair(a, b) else Pair(b, a)
                    edges.add(Triple(segment.first, segment.second, path.size))
                }

                map.isJunction(it) -> {
                    val a = path.first()
                    val b = it
                    val segment = if (a > b) Pair(a, b) else Pair(b, a)
                    edges.add(Triple(segment.first, segment.second, path.size))
                    if (it !in nodes) {
                        paths.add(listOf(it))
                        nodes.add(it)
                    }
                }

                else -> {
                    paths.add(path + it)
                }
            }
        }
    }

    val edgeMap = mutableMapOf<Vector, MutableSet<Pair<Vector, Int>>>()
    edges.forEach { (a, b, weight) ->
        if (a !in edgeMap) edgeMap[a] = mutableSetOf()
        if (b !in edgeMap) edgeMap[b] = mutableSetOf()
        edgeMap[a]!!.add(Pair(b, weight))
        edgeMap[b]!!.add(Pair(a, weight))
    }

    val nodePaths = mutableListOf<Pair<List<Vector>, Int>>()
    nodePaths.add(Pair(listOf(start), 0))

    var longestPath = 0

    while (nodePaths.isNotEmpty()) {
        val (history, length) = nodePaths.removeLast()
        if (history.last() == end) {
            if (length > longestPath) longestPath = length
        } else {
            edgeMap[history.last()]!!.forEach { (next, cost) ->
                if (next !in history) {
                    nodePaths.add(Pair(history + next, length + cost))
                }
            }
        }
    }

    return longestPath
}

fun Vector.m() = "$x,$y"

fun Grid.at(v: Vector): Char = this.getOrNull(v.y)?.getOrNull(v.x) ?: '#'

fun Grid.isJunction(v: Vector) =
    CompassDirection.values().map { at(v + it) }.count { it != '#' } > 2
