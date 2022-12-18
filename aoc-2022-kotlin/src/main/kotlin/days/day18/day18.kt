package days.day18

import lib.Reader
import lib.Vector
import lib.Vector3
import lib.checkAnswer
import lib.directions3D
import kotlin.reflect.KProperty1

fun main() {
    val input = Reader("day18.txt").strings()
    val exampleInput = Reader("day18-example.txt").strings()

    part1(exampleInput).checkAnswer(64)
    part1(input).checkAnswer(4536)
    println(part2(input))
}

fun part1(input: List<String>) =
    countExposedFaces(input.map(::parse).toSet())

fun part2(input: List<String>): Int {
    val points = input.map(::parse).toSet()
    val pointsSurrounding = points.flatMap { p -> p.surrounding }.toSet() - points

    val xRange = points.let { it.minOf { p -> p.x }..it.maxOf { p -> p.x } }.expand(1)
    val yRange = points.let { it.minOf { p -> p.y }..it.maxOf { p -> p.y } }.expand(1)
    val zRange = points.let { it.minOf { p -> p.z }..it.maxOf { p -> p.z } }.expand(1)

    val leftmost = points.minBy { it.x }
    val moldStart = leftmost + Vector3(-1, 0, 0)

    fun createMould(initial: Vector3): Set<Vector3> {
        tailrec fun go(front: Set<Vector3>, moldPoints: Set<Vector3>): Set<Vector3> {
            val newFront = (front.flatMap { it.neighbours }.toSet() - points - moldPoints)
                .filter { it.x in xRange && it.y in yRange && it.z in zRange }
                .toSet()
            return if (newFront.isEmpty()) moldPoints
            else go(newFront, moldPoints + newFront)
        }
        return go(setOf(initial), setOf(initial))
    }

    tailrec fun findInside(mold: Set<Vector3>, front: Set<Vector3>, inside: Set<Vector3>): Set<Vector3> {
        val expandedFront = front.flatMap { it.surrounding }.toSet()
        val newInside = front + inside
        val newFront = (expandedFront - newInside - mold)
        return if (newFront.isEmpty()) newInside
        else (findInside(mold, newFront, newInside))
    }

    fun fillMould(mould: Set<Vector3>) =
        xRange.flatMap { x ->
            yRange.flatMap { y ->
                zRange.mapNotNull { z ->
                    val v = Vector3(x, y, z)
                    if (v !in mould) v else null
                }
            }
        }.toSet()

    val mould = createMould(moldStart)
    val castOld = findInside(mould, setOf(leftmost), setOf(leftmost))
    val cast = fillMould(mould)

    printSideBySide(points, mould, cast)

    println((cast - castOld))

    return countExposedFaces(cast)
}

fun IntRange.expand(n: Int) = (this.first - n)..(this.last + n)

fun countExposedFaces(points: Set<Vector3>): Int {
    val neighbours = points.flatMap { n -> directions3D.map { d -> n + d } }
    val exposedNeighbours = neighbours - points
    exposedNeighbours.filter { it.z == 6 }.sorted().forEach { println(it) }
    exposedNeighbours.filter { it.z == 7 }.sorted().forEach { println(it) }
    exposedNeighbours.filter { it.z == 8 }.sorted().forEach { println(it) }
    return exposedNeighbours.size
}

fun printSideBySide(vararg setsOfPoints: Set<Vector3>) {
    val everything = setsOfPoints.fold(emptySet<Vector3>()) { acc, points -> acc + points  }
    val xRange = everything.getRange(Vector3::x)
    val yRange = everything.getRange(Vector3::y)
    val zRange = everything.getRange(Vector3::z)

    fun makeEmptyGrid(xRange: IntRange, yRange: IntRange): MutableList<MutableList<Char>> =
        MutableList(yRange.last - yRange.first + 1) {
            MutableList(xRange.last - xRange.first + 1) {
                '.'
            }
        }

    fun makeGrid(points: List<Vector>): List<List<Char>> {
        val grid = makeEmptyGrid(xRange, yRange)
        xRange.forEach { x ->
            points.forEach {
                grid[it.y - yRange.first][it.x - xRange.first] = '█'
            }
        }
        return grid
    }

    val zGroupings = setsOfPoints.map { it.groupBy(Vector3::z) { v -> Vector(v.x, v.y) } }

    zRange.forEach { z ->
        println(z)
        val grids = zGroupings.map { makeGrid(it[z] ?: emptyList()) }

        grids[0].indices.forEach { i ->
            val row = "${i.toString().padStart(2)} ${grids.joinToString(" ") { it[i].joinToString("") }}"
            println(row)
        }

        println()
    }
}

fun Collection<Vector3>.getRange(d: KProperty1<Vector3, Int>) =
    minOf(d)..maxOf(d)

fun parse(line: String) =
    line.split(",").map(String::toInt).let { Vector3(it[0], it[1], it[2]) }
