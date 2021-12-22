package days.day22

import lib.Reader
import lib.checkAnswer
import lib.time
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    val input = Reader("day22.txt").strings()
    val smallExample = Reader("day22-small-example.txt").strings()
    val exampleInput = Reader("day22-example.txt").strings()
    val exampleInput2 = Reader("day22-example-2.txt").strings()

    // Part 1 lost in the mists of time

    time(message = "Part2") {
        part2(input)
    }.checkAnswer(1267133912086024)
}

fun part2(input: List<String>) =
    input.map(::parse).fold(emptyList<Cuboid>()) { acc, rebootStep ->
        if (rebootStep.action == Status.ON) {
            acc.flatMap { it.minus(rebootStep.region) } + rebootStep.region
        } else {
            acc.flatMap { it.minus(rebootStep.region) }
        }
    }.sumOf { it.size }

fun parse(input: String) =
    """(on|off) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)""".toRegex().find(input)!!.destructured
        .let { (action, x1, x2, y1, y2, z1, z2) ->
            RebootStep(
                Status.from(action),
                Cuboid(
                    x1.toInt(), x2.toInt(),
                    y1.toInt(), y2.toInt(),
                    z1.toInt(), z2.toInt(),
                )
            )
        }

data class Cuboid(val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int) {
    fun minus(other: Cuboid): List<Cuboid> {
        if (!overlapsWith(other)) return listOf(this)

        val front = if (other.z1 > z1) Cuboid(x1, x2, y1, y2, z1, other.z1 - 1) else null

        val back = if (other.z2 < z2) Cuboid(x1, x2, y1, y2, other.z2 + 1, z2) else null

        val bottom = if (other.y1 > y1) {
            Cuboid(x1, x2, y1, other.y1 - 1, max(z1, other.z1), min(z2, other.z2))
        } else null

        val top = if (other.y2 < y2) {
            Cuboid(x1, x2, (other.y2 + 1), y2, max(z1, other.z1), min(z2, other.z2))
        } else null

        val left = if (other.x1 > x1) {
            Cuboid(x1, other.x1 - 1, max(y1, other.y1), min(y2, other.y2), max(z1, other.z1), min(z2, other.z2))
        } else null

        val right = if (other.x2 < x2) {
            Cuboid((other.x2 + 1), x2, max(y1, other.y1), min(y2, other.y2), max(z1, other.z1), min(z2, other.z2))
        } else null

        return listOfNotNull(front, back, bottom, top, left, right)
    }

    val size: Long
        get() = xRange.size().toLong() * yRange.size() * zRange.size()

    private fun overlapsWith(other: Cuboid) =
        xRange.overlapsWith(other.xRange) && yRange.overlapsWith(other.yRange) && zRange.overlapsWith(other.zRange)

    private val xRange = x1..x2
    private val yRange = y1..y2
    private val zRange = z1..z2
}

fun IntRange.size() = this.last - this.first + 1

fun IntRange.overlapsWith(other: IntRange): Boolean = !this.doesNotOverlapWith(other)

fun IntRange.doesNotOverlapWith(other: IntRange): Boolean =
    this.first > other.last || this.last < other.first

data class RebootStep(val action: Status, val region: Cuboid)

enum class Status {
    ON, OFF;

    companion object {
        fun from(s: String) = when (s) {
            "on" -> ON
            "off" -> OFF
            else -> error("Unknown action $s")
        }
    }
}
