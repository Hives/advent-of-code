package days.day09

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day09.txt").strings()
    val exampleInput = Reader("day09-example.txt").strings()

    time(iterations = 1_000, warmUpIterations = 100, message = "Part 1") {
        part1(input)
    }.checkAnswer(530)

    time(iterations = 1_000, warmUpIterations = 100, message = "Part 2") {
        part2(input)
    }.checkAnswer(1019494)
}

fun part1(input: List<String>): Int {
    val heightMap = HeightMap(input)
    return heightMap.getLowpoints()
        .sumOf { lowpoint -> heightMap.heightAt(lowpoint)!! + 1 }
}

fun part2(input: List<String>): Int {
    val heightMap = HeightMap(input)
    return heightMap.getLowpoints()
        .map { lowpoint -> heightMap.getBasin(lowpoint).size }
        .sortedDescending()
        .let { basinSizes -> basinSizes[0] * basinSizes[1] * basinSizes[2] }
}

class HeightMap(input: List<String>) {
    private val heights = input.map { row -> row.toCharArray().map { cell -> cell.digitToInt() } }
    private val cols = heights[0].size
    private val rows = heights.size

    fun heightAt(location: Vector): Int? {
        return when {
            (location.x < 0 || location.x >= cols) -> null
            (location.y < 0 || location.y >= rows) -> null
            else -> heights[location.y][location.x]
        }
    }

    fun getLowpoints(): List<Vector> =
        (heights.indices).flatMap { x ->
            (heights[x].indices).mapNotNull { y ->
                val location = Vector(x, y)
                if (
                    location.neighbours
                        .filter { it.isInHeightMap() }
                        .all { neighbour -> location.isLowerThan(neighbour, this) }
                ) location
                else null
            }
        }

    fun getBasin(startingPoint: Vector): Set<Vector> {
        tailrec fun go(basinPoints: Set<Vector>, lastExpansion: Set<Vector>): Set<Vector> {
            val expansion = lastExpansion.flatMap { point ->
                point.neighbours
                    .filter { it.isInHeightMap() }
                    .filterNot { it in basinPoints }
                    .filterNot { heightAt(it) == 9 }
            }.toSet()

            return if (expansion.isEmpty()) basinPoints
            else go(basinPoints + expansion, expansion)
        }

        return go(setOf(startingPoint), setOf(startingPoint))
    }

    private fun Vector.isInHeightMap() = this.x in 0 until cols && this.y in 0 until rows
}

fun Vector.isLowerThan(other: Vector, heightMap: HeightMap) = heightMap.heightAt(this)!! < heightMap.heightAt(other)!!