package days.day09

import lib.Reader
import lib.Vector
import lib.checkAnswer

fun main() {
    val input = Reader("day09.txt").strings()
    val exampleInput = Reader("day09-example.txt").strings()

    part1(input).checkAnswer(530)
}

fun part1(input: List<String>): Int {
    val heightMap = HeightMap(input)

    return heightMap.getLowpoints().sumOf { heightMap.at(it)!! + 1 }
}

class HeightMap(input: List<String>) {
    private val heights = input.map { row -> row.toCharArray().map { cell -> cell.digitToInt() } }
    private val cols = heights[0].size
    private val rows = heights.size

    fun getLowpoints(): List<Vector> =
        (0 until cols).flatMap { x ->
            (0 until rows).mapNotNull { y ->
                val location = Vector(x, y)
                val neighbourHeights = location.neighbours.mapNotNull { at(it) }
                if (neighbourHeights.all { it > at(location)!! }) location
                else null
            }
        }

    fun at(location: Vector): Int? {
        return when {
            (location.x < 0 || location.x >= cols) -> null
            (location.y < 0 || location.y >= rows) -> null
            else -> heights[location.y][location.x]
        }
    }
}