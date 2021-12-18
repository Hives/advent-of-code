package days.day17

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.sign

fun main() {
    val input = Reader("day17.txt").string()
    val exampleInput = Reader("day17-example.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(12090)

    time(message = "Part 2", iterations = 10, warmUpIterations = 5) {
        part2(input)
    }.checkAnswer(5059)
}

fun part1(input: String): Int? {
    val target = parse(input)
    return (0..1_000).asSequence().map { y -> Vector(15, y) }
        .mapNotNull { launch(it, target) }
        .mapNotNull { (trajectory, hitOrMiss) -> if (hitOrMiss == HitOrMiss.HIT) trajectory else null }
        .mapNotNull { trajectory -> trajectory.maxByOrNull { it.y } }
        .maxOfOrNull { it.y }
}

fun part2(input: String): Int {
    val target = parse(input)

    return (-250..250).flatMap { y ->
        (0..200).map { x ->
            Vector(x, y)
        }
    }.map { launch(it, target) }
        .filter { it.second == HitOrMiss.HIT }
        .size
}

fun launch(velocity: Vector, target: Target): Pair<Trajectory, HitOrMiss> {
    var location = Vector(0, 0)
    val trajectory = mutableListOf(location)
    var currentVelocity = velocity
    while (location.x <= target.xRange.last && location.y >= target.yRange.first) {
        location += currentVelocity
        trajectory.add(location)
        if (target.contains(location)) {
            return Pair(trajectory.toList(), HitOrMiss.HIT)
        }
        currentVelocity += Vector(-currentVelocity.x.sign, -1)
    }
    return Pair(trajectory.toList(), HitOrMiss.MISS)
}

fun printy(trajectory: Trajectory, target: Target) {
    val minX = 0
    val maxX = target.xRange.last

    val minY = target.yRange.first
    val maxY = trajectory.maxOf { it.y }
    (minY..maxY).reversed().forEach { y ->
        (minX..maxX).map { x ->
            val point = Vector(x, y)
            when {
                point in trajectory -> "#"
                target.contains(point) -> "T"
                else -> "."
            }
        }.joinToString("").also { println(it) }
    }
}

fun parse(input: String): Target =
    """target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""".toRegex().find(input)!!.destructured
        .let { (x1, x2, y1, y2) ->
            Target(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt())
        }

typealias Trajectory = List<Vector>

enum class HitOrMiss { HIT, MISS }

data class Target(val xRange: IntRange, val yRange: IntRange) {
    fun contains(point: Vector) = point.x in xRange && point.y in yRange
}