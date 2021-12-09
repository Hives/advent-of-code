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

fun part2(input: List<String>) {
    val heightMap = HeightMap(input)
    val lowPoints = heightMap.getLowpoints()
}

fun HeightMap.getBasin(lowpoint: Vector) {
    fun go(basin: Set<Vector>, neighbours: Set<Vector>) {
        neighbours.map { neighbour ->
            val neighboursOfNeighbour = neighbour.neighbours
            val notInBasin = neighbour.neighbours - basin
        }
    }

    val neighbours = lowpoint.neighbours
}

class HeightMap(input: List<String>) {
    private val heights = input.map { row -> row.toCharArray().map { cell -> cell.digitToInt() } }
    private val cols = heights[0].size
    private val rows = heights.size

    fun Vector.isInHeightMap() = this.x in 0 until cols && this.y in 0 until rows

    fun getLowpoints(): List<Vector> =
        (0 until cols).flatMap { x ->
            (0 until rows).mapNotNull { y ->
                val location = Vector(x, y)
                if (
                    location.neighbours
                        .filter { it.isInHeightMap() }
                        .all { neighbour -> location.isLowerThan(neighbour, this) }
                ) location
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

fun Vector.isLowerThan(other: Vector, heightMap: HeightMap) = heightMap.at(this)!! < heightMap.at(other)!!