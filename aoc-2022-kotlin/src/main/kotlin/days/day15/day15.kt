package days.day15

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.abs

fun main() {
    val input = Triple(Reader("day15.txt").strings(), 2_000_000, 4_000_000)
    val exampleInput = Triple(Reader("day15-example.txt").strings(), 10, 20)

    time(message = "Part 1", warmUpIterations = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(5256611)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(13337919186981L)
}

fun part1(input: Triple<List<String>, Int, Int>): Int {
    val targetRow = input.second;
    val sensorsAndBeacons = input.first.map(::parse)

    val resolvedIntersections = getIntersectionsAtRow(sensorsAndBeacons, targetRow).resolve()
    val pointsInResolvedIntersection = resolvedIntersections.flatMap { it.toList() }.toSet()
    val thingsOnTargetRow = sensorsAndBeacons.flatten().filter { it.y == targetRow }
    return (pointsInResolvedIntersection - thingsOnTargetRow.map { it.x }.toSet()).size
}

fun part2(input: Triple<List<String>, Int, Int>): Long {
    val maxY = input.third;
    val sensorsAndBeacons = input.first.map(::parse)

    val rowIntersections = (0..maxY).map { y ->
        val resolvedIntersection = getIntersectionsAtRow(sensorsAndBeacons, y).resolve()
        Pair(y, resolvedIntersection)
    }

    return rowIntersections.maxBy { (_, intersections) -> intersections.size }
        .let { (y, ranges) ->
            val x = ranges.first().last + 1
            x * 4_000_000L + y
        }
}

fun <T> List<Pair<T, T>>.flatten(): List<T> = flatMap { listOf(it.first, it.second) }

fun getIntersectionsAtRow(sensorsAndBeacons: List<Pair<Vector, Vector>>, targetRow: Int): List<IntRange> =
    sensorsAndBeacons.fold(emptyList<IntRange>()) { acc, (sensor, beacon) ->
        val distanceToBeacon = (beacon - sensor).manhattanDistance
        val distanceToTargetRow = abs(sensor.y - targetRow)
        val xDiff = distanceToBeacon - distanceToTargetRow
        if (xDiff < 0) {
            acc
        } else {
            val targetRowIntersection = (sensor.x - xDiff..sensor.x + xDiff)
            acc + listOf(targetRowIntersection)
        }
    }

fun List<IntRange>.resolve(): List<IntRange> =
    flatMap { listOf(RangeEnd.First(it.first), RangeEnd.Last(it.last)) }
        .sortedWith(compareBy({ it.value }, { it }))
        .fold(Triple(emptyList<IntRange>(), null as Int?, 0)) { acc, startOrEnd ->
            val (resolvedRanges, currentStart, depth) = acc
            when (startOrEnd) {
                is RangeEnd.First -> {
                    when (depth) {
                        0 -> Triple(resolvedRanges, startOrEnd.value, 1)
                        else -> Triple(resolvedRanges, currentStart, depth + 1)
                    }
                }

                is RangeEnd.Last -> {
                    when (depth) {
                        1 -> Triple(resolvedRanges + listOf(currentStart!!..startOrEnd.value), null, 0)
                        else -> Triple(resolvedRanges, currentStart, depth - 1)
                    }
                }
            }
        }.first

sealed class RangeEnd(open val value: Int) : Comparable<RangeEnd> {
    data class First(override val value: Int) : RangeEnd(value)
    data class Last(override val value: Int) : RangeEnd(value)

    override fun compareTo(other: RangeEnd): Int =
        when {
            this is First && other is Last -> -1
            this is Last && other is First -> 1
            else -> 0
        }
}

fun parse(line: String): Pair<Vector, Vector> {
    val r = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
    val (x1, y1, x2, y2) = r.find(line)!!.destructured
    return Pair(Vector(x1.toInt(), y1.toInt()), Vector(x2.toInt(), y2.toInt()))
}
