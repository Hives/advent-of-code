package days.day15

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = Triple(Reader("day15.txt").strings(), 2_000_000, 4_000_000)
    val exampleInput = Triple(Reader("day15-example.txt").strings(), 10, 20)

    time (message = "Part 1", warmUpIterations = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(5256611)

    time (message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(13337919186981L)
}

fun part1(input: Triple<List<String>, Int, Int>): Int {
    val targetRow = input.second;
    val sensorsAndBeacons = input.first.map(::parse)

    val foo = getIntersectionsAtRow(sensorsAndBeacons, targetRow).resolve()
    val bar = foo.fold(emptySet<Int>()) { acc, intRange -> acc + intRange.toSet() }
    val thingsOnTargetRow = sensorsAndBeacons.flatMap { it.toList() }.filter { it.y == targetRow }
    return (bar - thingsOnTargetRow.map { it.x }).size
}

fun part2(input: Triple<List<String>, Int, Int>): Long {
    val maxY = input.third;
    val sensorsAndBeacons = input.first.map(::parse)

    val baz = (0..maxY).map { y -> Pair(y, getIntersectionsAtRow(sensorsAndBeacons, y).resolve()) }

    return baz.maxBy { it.second.size }.let { (y, ranges) ->
        val x = ranges.first().last + 1
        x * 4_000_000L + y
    }
}

fun getIntersectionsAtRow(sensorsAndBeacons: List<Pair<Vector, Vector>>, targetRow: Int): List<IntRange> =
    sensorsAndBeacons.fold(emptyList<IntRange>()) { acc, (sensor, beacon) ->
        val distanceToBeacon = (beacon - sensor).manhattanDistance
        val distanceToTargetRow = abs(sensor.y - targetRow)
        val foo = distanceToBeacon - distanceToTargetRow
        if (foo < 0) {
            acc
        } else {
            val targetRowIntersection = (sensor.x - foo..sensor.x + foo)
            acc + listOf(targetRowIntersection)
        }
    }

fun List<IntRange>.resolve(): List<IntRange> {
    val startsAndEnds = this.fold(emptyList<Pair<Int, StartOrEnd>>()) { acc, intRange ->
        acc + Pair(intRange.first, StartOrEnd.START) + Pair(intRange.last, StartOrEnd.END)
    }

    return startsAndEnds.sortedBy { it.second.sortingVal }.sortedBy { it.first }.fold(Triple(emptyList<IntRange>(), null as Int?, 0)) { acc, startOrEnd ->
        val (ranges, currentStart, depth) = acc
        when (startOrEnd.second) {
            StartOrEnd.START -> {
                when (depth) {
                    0 -> {
                        require( currentStart == null) { "Depth was 0 but currentStart was not null"}
                        Triple(ranges, startOrEnd.first, 1)
                    }
                    else -> {
                        require( currentStart != null) { "Depth was > 0 but currentStart was null"}
                        Triple(ranges, currentStart, depth + 1)
                    }
                }
            }
            StartOrEnd.END -> when (depth) {
                1 -> Triple(ranges + listOf(currentStart!! .. startOrEnd.first), null, 0)
                else -> Triple(ranges, currentStart, depth - 1)
            }
        }
    }.first
}

enum class StartOrEnd(val sortingVal: Int) {
    START(0), END(1);
}

fun parse(line: String): Pair<Vector, Vector> {
    val r = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
    val (x1, y1, x2, y2) = r.find(line)!!.destructured
    return Pair(Vector(x1.toInt(), y1.toInt()), Vector(x2.toInt(), y2.toInt()))
}
