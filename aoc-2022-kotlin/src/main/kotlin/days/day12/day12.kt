package days.day12

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day12.txt").strings().map(String::toList)
    val exampleInput = Reader("day12-example.txt").strings().map(String::toList)

    time(message = "Part 1", warmUpIterations = 5, iterations = 10) {
        part1(input)
    }.checkAnswer(456)

    time(message = "Part 2", warmUpIterations = 5, iterations = 10) {
        part2(input)
    }.checkAnswer(454)
}

fun part1(heightMap: HeightMap) = doIt(
    heightMap = heightMap,
    isStart = { v, map -> map.at(v) == 'S' },
    isEnd = { v, map -> map.at(v) == 'E' },
    direction = Direction.FORWARDS
)

fun part2(heightMap: HeightMap) = doIt(
    heightMap = heightMap,
    isStart = { v, map -> map.at(v) == 'E' },
    isEnd = { v, map -> map.at(v) == 'a' },
    direction = Direction.BACKWARDS
)

fun doIt(
    heightMap: HeightMap,
    isStart: (Vector, HeightMap) -> Boolean,
    isEnd: (Vector, HeightMap) -> Boolean,
    direction: Direction
): Int {
    val allCoords = heightMap.indices.flatMap { y -> heightMap[y].indices.map { x -> Vector(x, y) } }
    val start = allCoords.first { isStart(it, heightMap) }
    val distances = allCoords.associateWith { Int.MAX_VALUE }.toMutableMap()
    distances[start] = 0

    val remaining = allCoords.toMutableSet()

    while (true) {
        val v = remaining.minBy { distances[it]!! }
        remaining.remove(v)
        if (isEnd(v, heightMap)) return distances[v]!!

        v.neighbours.filter { it in remaining }.forEach { neighbour ->
            val (first, second) = when (direction) {
                Direction.FORWARDS -> Pair(v, neighbour)
                Direction.BACKWARDS -> Pair(neighbour, v)
            }
            if (heightMap.height(second) <= heightMap.height(first) + 1) {
                val distance = distances[v]!! + 1
                if (distance < distances[neighbour]!!) {
                    distances[neighbour] = distance
                }
            }
        }
    }
}

enum class Direction { FORWARDS, BACKWARDS }

typealias HeightMap = List<List<Char>>

fun HeightMap.at(v: Vector): Char = this[v.y][v.x]
fun HeightMap.height(v: Vector): Int =
    when {
        at(v) == 'S' -> 'a'.code
        at(v) == 'E' -> 'z'.code
        else -> at(v).code
    }
