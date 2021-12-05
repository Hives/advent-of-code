package days.day05

import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val input = Reader("day05.txt").strings()
    val exampleInput = Reader("day05-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(4873)

    time(message = "Part 2") {
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
    this.flatMap { it.points }
        .groupBy { it }
        .count { (_, vectors) -> vectors.size > 1 }

fun parseInput(input: List<String>): List<Line> =
    input
        .map { """(\d+),(\d+) -> (\d+),(\d+)""".toRegex().find(it)!!.destructured }
        .map { (x1, y1, x2, y2) ->
            Line(Vector(x1.toInt(), y1.toInt()), Vector(x2.toInt(), y2.toInt()))
        }

data class Line(val start: Vector, val end: Vector) {
    val points: List<Vector> = run {
        val difference = (end - start)
        val direction = difference.normalise()
        val distance = max(difference.x.absoluteValue, difference.y.absoluteValue)
        (0..distance)
            .map { steps -> start + (direction * steps) }
    }

    private val isVertical = start.x == end.x
    private val isHorizontal = start.y == end.y
    val isHorizontalOrVertical = isHorizontal || isVertical
}

fun Vector.normalise() = Vector(x.sign, y.sign)