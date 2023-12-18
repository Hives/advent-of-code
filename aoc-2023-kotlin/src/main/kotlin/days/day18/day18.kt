package days.day18

import days.day18.Direction.Down
import days.day18.Direction.Left
import days.day18.Direction.Right
import days.day18.Direction.Up
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day18/input.txt").strings()
    val exampleInput = Reader("/day18/example-1.txt").strings()

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
    val path = createPath(input.map(::parse))
    val insideStart = path.minBy { it.x } + Up + Right // 🤪
    if (insideStart in path) throw Error("insidePath was part of path?!")
    val inside = mutableSetOf(insideStart)
    var front = setOf(insideStart)

    while (front.isNotEmpty()) {
        front = front.expand().filter { it !in path && it !in inside }.toSet()
        inside.addAll(front)
    }

    return inside.count() + path.count()
}

fun part2(input: List<String>): Int {
    return -1
}

fun createPath(instructions: List<Instruction>): Set<Vector> {
    val path = mutableSetOf<Vector>()
    var current = Vector(0, 0)
    instructions.forEach { instruction ->
        var n = 0
        while (n < instruction.n) {
            current += instruction.d
            path.add(current)
            n++
        }
    }
    return path
}

fun parse(line: String): Instruction {
    val match = Regex("""([LRUD]) (\d+) \(#(.*)\)""").find(line)
    val (d, n, hash) = match!!.destructured
    return Instruction(Direction.from(d), n.toInt(), hash)
}

fun Set<Vector>.printy() {
    val minX = this.minOf { it.x }
    val minY = this.minOf { it.y }
    val maxX = this.maxOf { it.x }
    val maxY = this.maxOf { it.y }
    List(maxY - minY + 1) { y ->
        List(maxX - minX + 1) { x ->
            val v = Vector(x + minX, y + minY)
            if (v in this) '#' else '.'
        }.joinToString("").also(::println)
    }
}

data class Instruction(val d: Direction, val n: Int, val hash: String)

fun Set<Vector>.expand() =
    flatMap { point -> Direction.all.map { d -> point + d } }.toSet()

operator fun Vector.plus(other: Direction) = Vector(
    x = this.x + other.v.x,
    y = this.y + other.v.y,
)

sealed class Direction(val v: Vector) {
    object Left : Direction(Vector(-1, 0))
    object Right : Direction(Vector(1, 0))
    object Up : Direction(Vector(0, -1))
    object Down : Direction(Vector(0, 1))
    companion object {
        fun from(s: String) =
            when (s) {
                "L" -> Left
                "R" -> Right
                "U" -> Up
                "D" -> Down
                else -> throw Error("Oh no")
            }
        val all = listOf(Left, Right, Up, Down)
    }
}
