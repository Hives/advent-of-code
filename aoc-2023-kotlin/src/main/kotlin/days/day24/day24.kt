package days.day24

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.sign
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day24/input.txt").strings()
    val exampleInput = Reader("/day24/example-1.txt").strings()

    // 25308 is too high
    println(part1(exampleInput))

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<String>): Int {
    val hailstones = input.map(::parse)

    val exampleBoundaries = Pair(7L, 27L)
    val realBoundaries = Pair(200000000000000, 400000000000000)

    var count = 0

    hailstones.indices.forEach { i1 ->
        ((i1 + 1) until hailstones.size).forEach { i2 ->
            val h1 = hailstones[i1]
            val h2 = hailstones[i2]
            val i = findIntersection(h1, h2)
            if (i != null && isInFuture(i, h1) && isInFuture(i, h2)) {
                if (isInsideBoundaries(i, exampleBoundaries)) count++
            }
        }
    }

    return count
}

fun part2(input: List<String>): Int {
    return -1
}

fun isInsideBoundaries(point: Pair<Double, Double>, boundaries: Pair<Long, Long>) =
    point.first >= boundaries.first && point.second <= boundaries.second &&
            point.second >= boundaries.first && point.second <= boundaries.second

fun isInFuture(point: Pair<Double, Double>, hailstone: Hailstone): Boolean =
    sign(point.first - hailstone.pos.x) == sign(hailstone.vel.x.toDouble()) &&
            sign(point.second - hailstone.pos.y) == sign(hailstone.vel.y.toDouble())

fun findIntersection(h1: Hailstone, h2: Hailstone): Pair<Double, Double>? {
    if (areParallel(h1.vel, h2.vel)) return null

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
            // h2 || h1 - shouldn't happen
            throw Exception("paths were parallel, but that shouldn't have happened: $h1 $h2")
        }

        else -> {
            val x = (c2 - c1) / (m1 - m2)
            val y = (m1 * (c2 - c1) / (m1 - m2)) + c1
            Pair(x, y)
        }
    }
}

fun getMAndC(h: Hailstone): Pair<Double?, Double?> {
    if (h.vel.x == 0) return Pair(null, null)
    val m = h.vel.y.toDouble() / h.vel.x
    val c = h.pos.y - ((h.pos.x.toDouble() / h.vel.x) * h.vel.y)
    return Pair(m, c)
}

fun areParallel(v1: Velocity, v2: Velocity): Boolean {
    val ratios = listOf(
        v1.x.toDouble() / v2.x,
        v1.y.toDouble() / v2.y,
//        v1.z.toDouble() / v2.z,
    )
    return ratios.all { it == ratios[0] }
}

fun parse(line: String): Hailstone =
    line.split(" @ ").let { (p, v) ->
        Hailstone(
            p.split(", ").let { (x, y, z) -> Position(x.trim().toLong(), y.trim().toLong(), z.trim().toLong()) },
            v.split(", ").let { (x, y, z) -> Velocity(x.trim().toInt(), y.trim().toInt(), z.trim().toInt()) },
        )
    }

data class Hailstone(val pos: Position, val vel: Velocity)
data class Position(val x: Long, val y: Long, val z: Long)
data class Velocity(val x: Int, val y: Int, val z: Int)
