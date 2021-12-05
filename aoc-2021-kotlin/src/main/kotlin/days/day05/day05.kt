package days.day05

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.lang.Integer.max
import kotlin.math.absoluteValue

fun main() {
    val input = Reader("day05.txt").strings()
    val exampleInput = Reader("day05-example.txt").strings()

    time {
        part1(input)
    }.checkAnswer(4873)

    time {
        part2(input)
    }.checkAnswer(19472)
}

fun part1(input: List<String>) =
    parseInput(input)
        .filter { it.isHorizontalOrVertical }
        .countPointsCoveredByMoreThanOneLine()

fun part2(input: List<String>) =
    parseInput(input)
        .countPointsCoveredByMoreThanOneLine()

fun List<Line>.countPointsCoveredByMoreThanOneLine() =
    this.flatMap { it.covers }
        .groupingBy { it }
        .eachCount()
        .count { (_, count) -> count > 1 }

fun parseInput(input: List<String>): List<Line> =
    input
        .map { """(\d+),(\d+) -> (\d+),(\d+)""".toRegex().find(it)!!.destructured }
        .map { (x1, y1, x2, y2) ->
            Line(Vector(x1.toInt(), y1.toInt()), Vector(x2.toInt(), y2.toInt()))
        }

data class Line(val start: Vector, val end: Vector) {
    val covers: List<Vector>
        get() {
            val difference = (this.end - this.start)
            val steps = max(difference.x.absoluteValue, difference.y.absoluteValue)
            val xInc = normalise(difference.x)
            val yInc = normalise(difference.y)
            return (0..steps)
                .map { Vector(start.x + (it * xInc), start.y + (it * yInc)) }
        }

    val isHorizontalOrVertical
        get() = isHorizontal || isVertical
    private val isVertical
        get() = start.x == end.x
    private val isHorizontal
        get() = start.y == end.y
}

fun normalise(n: Int) = if (n == 0) 0 else n / n.absoluteValue