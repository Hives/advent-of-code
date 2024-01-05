package days.day24

import lib.Reader
import lib.checkAnswer
import lib.time
import java.io.File
import java.io.PrintWriter
import kotlin.math.sign
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day24/input.txt").strings()
    val exampleInput = Reader("/day24/example-1.txt").strings()

    // 25308 is too high
    // 25304 is too high
    // 25269 is... not right
    // 19968 is too low
    println(part1(input))

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

    File("/home/hives/tmp/aoc-day-24-method-2.txt").printWriter().use { out ->
        return hailstones.flatMapIndexed { index, h1 ->
            hailstones.drop(index + 1).map { h2 ->
                Pair(h1, h2)
            }
        }.count { (h1, h2) -> crossInFuture(h1, h2, PrintWriter(System.out)) }
    }

}

fun crossInFuture(h1: Hailstone, h2: Hailstone, out: PrintWriter): Boolean {
    out.println("---")
    out.println(h1)
    out.println(h2)

    val i = findIntersection(h1, h2, out)

    return when {
        i == null -> {
            out.println("X: no cross")
            false
        }

        !isInFuture(i, h1) && !isInFuture(i, h2) -> {
            out.println("X: is in past for both: $i")
            false
        }

        !isInFuture(i, h1) -> {
            out.println("X: is in past for h1: $i")
            false
        }

        !isInFuture(i, h2) -> {
            out.println("X: is in past for h2: $i")
            false
        }

        !isInsideBoundaries(i) -> {
            out.println("X: outside boundaries: $i")
            false
        }

        else -> {
            out.println("Y: is good: $i")
            true
        }
    }
}

fun part2(input: List<String>): Int {
    return -1
}

fun isInsideBoundaries(point: Pair<Double, Double>): Boolean {
    val exampleBoundaries = Pair(7L, 27L)
    val realBoundaries = Pair(200000000000000, 400000000000000)

    val boundaries = realBoundaries

    return point.first >= boundaries.first && point.second <= boundaries.second &&
            point.second >= boundaries.first && point.second <= boundaries.second
}

fun isInFuture(point: Pair<Double, Double>, hailstone: Hailstone): Boolean =
    sign(point.first - hailstone.pos.x) == sign(hailstone.vel.x.toDouble()) &&
            sign(point.second - hailstone.pos.y) == sign(hailstone.vel.y.toDouble())

fun findIntersection2(h1: Hailstone, h2: Hailstone, out: PrintWriter): Pair<Double, Double>? {
    // from wikipedia, determinant method

    out.println("m1: asdasd")
    out.println("m2: asdasd")

    val x1 = h1.pos.x.toDouble()
    val y1 = h1.pos.y.toDouble()
    val x2 = x1 + h1.vel.x
    val y2 = y1 + h1.vel.y

    val x3 = h2.pos.x.toDouble()
    val y3 = h2.pos.y.toDouble()
    val x4 = x3 + h2.vel.x
    val y4 = y3 + h2.vel.y

    val denominator = ((x1 - x2) * (y3 - y4)) - ((y1 - y2) * (x3 - x4))
    if (denominator == 0.toDouble()) return null

    val numeratorX = (((x1 * y2) - (y1 * x2)) * (x3 - x4)) - ((x1 - x2) * ((x3 * y4) - (y3 * x4)))
    val numeratorY = (((x1 * y2) - (y1 * x2)) * (y3 - y4)) - ((y1 - y2) * ((x3 * y4) - (y3 * x4)))

    return Pair(numeratorX / denominator, numeratorY / denominator)
}

fun findIntersection(h1: Hailstone, h2: Hailstone, out: PrintWriter): Pair<Double, Double>? {
    if (h1.vel.x == 0 || h2.vel.x == 0) throw Exception("was vertical?!")

    val (m1, c1) = getMAndC(h1)
    val (m2, c2) = getMAndC(h2)
    out.println("m1: $m1, c1: $c1")
    out.println("m2: $m2, c2: $c2")

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
//    if (h.vel.x == 0) return Pair(null, null)
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
