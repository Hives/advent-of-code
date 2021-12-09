package days.day09

import lib.Reader
import lib.Vector
import lib.checkAnswer

fun main() {
    val input = Reader("day09.txt").strings()
    val exampleInput = Reader("day09-example.txt").strings()

    part1(input).checkAnswer(530)

    part2(input)
}

fun part1(input: List<String>): Int {
    val heightMap = HeightMap(input)

    return heightMap.getLowpoints().sumOf { heightMap.heightAt(it)!! + 1 }
}

fun part2(input: List<String>) {
    val heightMap = HeightMap(input)
    val lowpoints = heightMap.getLowpoints()
    lowpoints.map { heightMap.getBasin(it).size }
        .sortedDescending()
        .let { it[0] * it[1] * it[2] }
        .also { println(it) }
}

fun HeightMap.getBasin(lowpoint: Vector): Set<Vector> {
    tailrec fun go(basinPoints: Set<Vector>, testPoints: Set<Vector>): Set<Vector> {
        println("-- go --")
        println("---- basin: $basinPoints")
        val expansion = testPoints.flatMap { point ->
            println("---- $point")
            val neighbours = point.neighbours
            println("---- $neighbours")
            neighbours
                .filter { it.isInHeightMap() }
                .also {
                    println("---- in heightmap $it")
                }
                .filterNot { it in basinPoints }
                .filterNot { heightAt(it) == 9 }
                .filter { heightAt(it)!! > heightAt(point)!! }
        }.toSet()
        println("---- expansion: $expansion")

        return if (expansion.isEmpty()) run {
            println("-- final basin: $basinPoints")
            println("-- size: ${basinPoints.size}\n")
            basinPoints
        }
        else go(basinPoints + expansion, expansion)
    }

    return go(setOf(lowpoint), setOf(lowpoint))
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

    fun heightAt(location: Vector): Int? {
        return when {
            (location.x < 0 || location.x >= cols) -> null
            (location.y < 0 || location.y >= rows) -> null
            else -> heights[location.y][location.x]
        }
    }
}

fun Vector.isLowerThan(other: Vector, heightMap: HeightMap) = heightMap.heightAt(this)!! < heightMap.heightAt(other)!!