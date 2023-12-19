package days.day18.part2

import days.day18.part2.Direction.*
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.abs

fun main() {
    val input = Reader("/day18/input.txt").strings()
    val exampleInput = Reader("/day18/example-1.txt").strings()

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(40654918441248)
}

fun part2(input: List<String>): Long {
    val instructions = input.map(::parse2)
    val path = createPath(instructions)

    val (horizontals, verticals) = path.partition { it.isHorizontal() }
    val infiniteHorizontals = horizontals.map { it.first.y }.toSet().sorted()
    val infiniteVerticals = verticals.map { it.first.x }.toSet().sorted()
    val minHorizontalGap = infiniteHorizontals.sorted().windowed(2).minOf { (first, second) -> second - first }
    val minVerticalGap = infiniteVerticals.sorted().windowed(2).minOf { (first, second) -> second - first }
    require(minVerticalGap > 1 && minHorizontalGap > 1)

//    """
//        <svg viewBox="${infiniteVerticals.first()} ${infiniteHorizontals.first()} ${infiniteVerticals.last() - infiniteVerticals.first()} ${infiniteHorizontals.last() - infiniteHorizontals.first()}">
//          <path stroke="red" fill="none" stroke-width="100000" d="
//            M 0,0
//            ${instructions.joinToString(" ") { instruction ->
//               val v = instruction.d.v * instruction.n
//               "l ${v.x},${v.y}"
//            }}
//          " />
//        </svg>
//    """.trimIndent().also { println(it) }

    var internalsCount = 0
    var internals = 0L
    var gridEdges = mutableListOf<PathSegment>()
    var gridCorners = mutableSetOf<Vector>()

    infiniteHorizontals.windowed(2).forEach { (minY, maxY) ->
        infiniteVerticals.windowed(2).forEach { (minX, maxX) ->
            val targetPoint = Vector(minX + 1, minY + 1)
            if (targetPoint.y in infiniteHorizontals) throw Error("Something's not right here")
            val crossingsOfVerticals = verticals.count { (start, end) ->
                val ys = listOf(start.y, end.y).sorted()
                start.x < targetPoint.x && ys[0] < targetPoint.y && ys[1] > targetPoint.y
            }
            val isInsidePath = crossingsOfVerticals.isOdd()
            if (isInsidePath) {
                internalsCount += 1

                val dY = maxY - minY - 1 // this just gets the inside
                val dX = maxX - minX - 1
                val inside = dY.toLong() * dX.toLong()
                internals += inside

                val topLeft = Vector(minX, minY)
                val bottomLeft = Vector(minX, maxY)
                val topRight = Vector(maxX, minY)
                val bottomRight = Vector(maxX, maxY)

                val corners = listOf(topLeft, bottomLeft, topRight, bottomRight)
                gridCorners.addAll(corners)

                val edges = listOf(
                    Pair(topLeft + Right, topRight + Left),
                    Pair(bottomLeft + Right, bottomRight + Left),
                    Pair(topLeft + Down, bottomLeft + Up),
                    Pair(topRight + Down, bottomRight + Up)
                )
                gridEdges.addAll(edges)
            }
        }
    }

    println(internalsCount)

    val pathLength = path.sumOf {
        if (it.isVertical()) {
            abs(it.first.y - it.second.y)
        } else {
            abs(it.first.x - it.second.x)
        }
    }

    val gridEdgesSum = gridEdges.toSet().sumOf {
        if (it.isVertical()) {
            abs(it.first.y.toLong() - it.second.y.toLong()) + 1
        } else {
            abs(it.first.x.toLong() - it.second.x.toLong()) + 1
        }
    }

    val total = internals + gridEdgesSum + gridCorners.size

    return total
}

fun PathSegment.isHorizontal() = first.y == second.y
fun PathSegment.isVertical() = first.x == second.x

fun createPath(instructions: List<Instruction>): Path {
    val path = mutableListOf<Pair<Vector, Vector>>()
    val start = Vector(0, 0)
    var current = start
    instructions.forEach { instruction ->
        val next = (instruction.d.v * instruction.n) + current
        path.add(Pair(current, next))
        current = next
    }
    return path
}

fun parse(line: String): Instruction {
    val match = Regex("""([LRUD]) (\d+)""").find(line)
    val (d, n) = match!!.destructured
    return Instruction(Direction.from(d), n.toInt())
}

fun parse2(line: String): Instruction {
    val match = Regex("""\(#(.*)\)""").find(line)
    val (hash) = match!!.destructured
    val distance = hash.substring(0..4).toInt(16)
    return Instruction(Direction.from(hash.substring(5)), distance)
}

data class Instruction(val d: Direction, val n: Int)

operator fun Vector.plus(other: Direction) = Vector(
    x = this.x + other.v.x,
    y = this.y + other.v.y,
)

sealed class Direction(val v: Vector) {
    object Left : Direction(Vector(-1, 0)) {
        override fun toString() = "Left"
    }

    object Right : Direction(Vector(1, 0)) {
        override fun toString() = "Right"
    }

    object Up : Direction(Vector(0, -1)) {
        override fun toString() = "Up"
    }

    object Down : Direction(Vector(0, 1)) {
        override fun toString() = "Down"
    }

    companion object {
        fun from(s: String) =
            when (s) {
                "R" -> Right
                "D" -> Down
                "L" -> Left
                "U" -> Up
                "0" -> Right
                "1" -> Down
                "2" -> Left
                "3" -> Up
                else -> throw Error("Oh no")
            }
    }
}

fun Int.isOdd() = this % 2 == 1

typealias Path = List<PathSegment>
typealias PathSegment = Pair<Vector, Vector>
