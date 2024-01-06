package days.day24.part1

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.sign

fun main() {
    val input = Reader("/day24/input.txt").strings()
    val exampleInput = Reader("/day24/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(25261)
}

fun part1(input: List<String>): Int {
    val hailstones = input.map(::parse)

    return hailstones.flatMapIndexed { index, h1 ->
        hailstones.drop(index + 1).map { h2 ->
            Pair(h1, h2)
        }
    }.count { (h1, h2) -> crossInFuture(h1, h2) }
}

fun crossInFuture(h1: Hailstone, h2: Hailstone): Boolean {
    val i = findIntersection(h1, h2)

    return when {
        i == null -> {
            false
        }

        !isInFuture(i, h1) || !isInFuture(i, h2) -> {
            false
        }

        !isInsideBoundaries(i) -> {
            false
        }

        else -> {
            true
        }
    }
}

fun isInsideBoundaries(point: Pair<Double, Double>): Boolean {
    val exampleBoundaries = Pair(7L, 27L)
    val realBoundaries = Pair(200000000000000, 400000000000000)

    val boundaries = realBoundaries

    return point.first >= boundaries.first && point.first <= boundaries.second &&
            point.second >= boundaries.first && point.second <= boundaries.second
}

fun isInFuture(point: Pair<Double, Double>, hailstone: Hailstone): Boolean =
    sign(point.first - hailstone.pos.x) == sign(hailstone.vel.x.toDouble()) &&
            sign(point.second - hailstone.pos.y) == sign(hailstone.vel.y.toDouble())

fun findIntersection(h1: Hailstone, h2: Hailstone): Pair<Double, Double>? {
    val (m1, c1) = getMAndC(h1)
    val (m2, c2) = getMAndC(h2)

    return when {
        m1 == null || c1 == null -> {
            // h1 is vertical
            TODO()
        }

        m2 == null || c2 == null -> {
            // h2 is vertical
            TODO()
        }

        m1 == m2 -> {
            // h2 || h1
            return null
        }

        else -> {
            val x = (c2 - c1) / (m1 - m2)
            val y = (m1 * (c2 - c1) / (m1 - m2)) + c1
            Pair(x, y)
        }
    }
}

fun getMAndC(h: Hailstone): Pair<Double?, Double?> {
    val m = h.vel.y.toDouble() / h.vel.x
    val c = h.pos.y - ((h.pos.x.toDouble() / h.vel.x) * h.vel.y)
    return Pair(m, c)
}

fun parse(line: String): Hailstone =
    line.split(" @ ").let { (p, v) ->
        Hailstone(
            p.split(", ").let { (x, y, z) -> Position(x.toLong(), y.toLong(), z.toLong()) },
            v.split(", ").let { (x, y, z) -> Velocity(x.toInt(), y.toInt(), z.toInt()) },
        )
    }

data class Hailstone(val pos: Position, val vel: Velocity)
data class Position(val x: Long, val y: Long, val z: Long)
data class Velocity(val x: Int, val y: Int, val z: Int)
