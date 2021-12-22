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
                    xRange = x1.toInt()..x2.toInt(),
                    yRange = y1.toInt()..y2.toInt(),
                    zRange = z1.toInt()..z2.toInt(),
                )
            )
        }

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun minus(other: Cuboid): List<Cuboid> {
        if (!overlapsWith(other)) return listOf(this)

        val front = if (other.zRange.first > zRange.first) {
            Cuboid(xRange, yRange, zRange.first until other.zRange.first)
        } else null

        val back = if (other.zRange.last < zRange.last) {
            Cuboid(xRange, yRange, (other.zRange.last + 1)..zRange.last)
        } else null

        val bottom = if (other.yRange.first > yRange.first) {
            Cuboid(
                xRange = xRange,
                yRange = yRange.first until other.yRange.first,
                zRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last)
            )
        } else null

        val top = if (other.yRange.last < yRange.last) {
            Cuboid(
                xRange = xRange,
                yRange = (other.yRange.last + 1)..yRange.last,
                zRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last)
            )
        } else null

        val left = if (other.xRange.first > xRange.first) {
            Cuboid(
                xRange = xRange.first until other.xRange.first,
                yRange = max(yRange.first, other.yRange.first)..min(yRange.last, other.yRange.last),
                zRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last)
            )
        } else null

        val right = if (other.xRange.last < xRange.last) {
            Cuboid(
                xRange = (other.xRange.last + 1)..xRange.last,
                yRange = max(yRange.first, other.yRange.first)..min(yRange.last, other.yRange.last),
                zRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last)
            )
        } else null

        return listOf(front, back, bottom, top, left, right).filterNotNull().filterNot { it.isEmpty }
    }

    fun overlapsWith(other: Cuboid) =
        xRange.overlapsWith(other.xRange) && yRange.overlapsWith(other.yRange) && zRange.overlapsWith(other.zRange)

    val size: Long
        get() = xRange.size().toLong() * yRange.size() * zRange.size()

    val isEmpty: Boolean
        get() = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()
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
