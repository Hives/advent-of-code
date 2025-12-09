package days.day09

import kotlin.math.abs
import kotlin.system.exitProcess
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day09/input.txt").vectors()
    val (part1, part2) = Reader("/day09/answers.txt").longs()
    val exampleInput = Reader("/day09/example-1.txt").vectors()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(warmUp = 2, iterations = 5, message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<Vector>) =
    input.getPairs().maxOf { it.size() }

fun part2(input: List<Vector>): Long {
    val edges = (input + input.first()).windowed(2).map { Pair(it[0], it[1]) }
    return input.getPairs().filter { cornerPair ->
        edges.none { edge -> edge.disqualifies(cornerPair) }
    }.maxOf { it.size() }
}

fun Pair<Vector, Vector>.size(): Long {
    val diagonal = second - first
    return (abs(diagonal.x) + 1) * (abs(diagonal.y) + 1)
}

fun <T> Pair<T, T>.contains(x: T) = first == x || second == x

fun Pair<Vector, Vector>.disqualifies(rect: Pair<Vector, Vector>): Boolean {
    val (minX, maxX) = listOf(rect.first.x, rect.second.x).sorted()
    val (minY, maxY) = listOf(rect.first.y, rect.second.y).sorted()

    return when {
        first.x == second.x -> {
            // vertical
            val (top, bottom) = listOf(first.y, second.y).sorted()
            val alignedX = first.x > minX && first.x < maxX
            val overlappingTop = top <= minY && bottom > minY
            val overlappingBottom = top < maxY && bottom >= maxY
            val inside = Pair(minY, maxY).contains(first.y, second.y)
            alignedX && (overlappingTop || overlappingBottom || inside)
        }

        first.y == second.y -> {
            // horizontal
            val (left, right) = listOf(first.x, second.x).sorted()
            val alignedY = first.y > minY && first.y < maxY
            val overlapsLeft = left <= minX && right > minX
            val overlapsRight = left < maxX && right >= maxX
            val inside = Pair(minX, maxX).contains(first.x, second.x)
            alignedY && (overlapsLeft || overlapsRight || inside)
        }

        else -> throw Error("Didn't expect this?!")
    }
}

fun Pair<Long, Long>.contains(vararg ns: Long): Boolean {
    val range = listOf(first, second).sorted().let { it[0]..it[1] }
    return ns.all { range.contains(it) }
}

fun List<Vector>.getPairs(): List<Pair<Vector, Vector>> {
    val output = mutableListOf<Pair<Vector, Vector>>()
    forEachIndexed { i, first ->
        drop(i + 1).forEach { second ->
            if (first < second) output.add(Pair(first, second))
            else output.add(Pair(second, first))
        }
    }
    return output
}
