package days.day18

import lib.Reader
import lib.Vector
import lib.Vector3
import lib.checkAnswer
import lib.directions3D
import lib.time
import kotlin.reflect.KProperty1

fun main() {
    val input = Reader("day18.txt").strings()
    val samsInput = Reader("day18-sams-input.txt").strings()
    val pjsInput = Reader("day18-pjs-input.txt").strings()
    val exampleInput = Reader("day18-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(4536)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(2606)

    time(message = "Part 1 Sam's input") {
        part1(samsInput)
    }.checkAnswer(3496)

    time(message = "Part 2 Sam's input") {
        part2(samsInput)
    }.checkAnswer(2064)

    time(message = "Part 1 PJ's input") {
        part1(pjsInput)
    }.checkAnswer(4192)

    time(message = "Part 2 PJ's input") {
        part2(pjsInput)
    }.checkAnswer(2520)
}

fun part1(input: List<String>) =
    countExposedFaces(input.map(::parse).toSet())

fun part2(input: List<String>): Int {
    val points = input.map(::parse).toSet()

    val ranges = points.getRanges()
    val xRange = ranges.first.expand(1)
    val yRange = ranges.second.expand(1)
    val zRange = ranges.third.expand(1)

    val leftmost = points.minBy { it.x }
    val mouldStarter = leftmost + Vector3(-1, 0, 0)

    fun createMould(initial: Vector3): Set<Vector3> {
        tailrec fun go(front: Set<Vector3>, mouldPoints: Set<Vector3>): Set<Vector3> {
            val newFront = (front.flatMap { it.neighbours }.toSet() - points - mouldPoints)
                .filter { it.x in xRange && it.y in yRange && it.z in zRange }
                .toSet()
            return if (newFront.isEmpty()) mouldPoints
            else go(newFront, mouldPoints + newFront)
        }
        return go(setOf(initial), setOf(initial))
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

    val mould = createMould(mouldStarter)
    val cast = fillMould(mould)

//    printSideBySide(points, mould, cast)

    return countExposedFaces(cast)
}

fun IntRange.expand(n: Int) = (this.first - n)..(this.last + n)

fun countExposedFaces(points: Set<Vector3>): Int {
    val neighbours = points.flatMap { n -> directions3D.map { d -> n + d } }
    val exposedNeighbours = neighbours.filter { it !in points }
    return exposedNeighbours.size
}

fun printSideBySide(vararg setsOfPoints: Set<Vector3>) {
    val everything = setsOfPoints.fold(emptySet<Vector3>()) { acc, points -> acc + points  }
    val (xRange, yRange, zRange) = everything.getRanges()

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

fun Collection<Vector3>.getRanges(): Triple<IntRange, IntRange, IntRange> =
    Triple(
        minOf(Vector3::x)..maxOf(Vector3::x),
        minOf(Vector3::y)..maxOf(Vector3::y),
        minOf(Vector3::z)..maxOf(Vector3::z),
    )


fun makeEmptyGrid(xRange: IntRange, yRange: IntRange): MutableList<MutableList<Char>> =
    MutableList(yRange.last - yRange.first + 1) {
        MutableList(xRange.last - xRange.first + 1) {
            '.'
        }
    }

fun parse(line: String) =
    line.split(",").map(String::toInt).let { Vector3(it[0], it[1], it[2]) }
