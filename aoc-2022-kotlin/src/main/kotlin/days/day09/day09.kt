package days.day09

import lib.Direction
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.math.sign

fun main() {
    val input = Reader("day09.txt").strings()
    val exampleInput = Reader("day09-example.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(6339)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(2541)
}

fun part1(input: List<String>) = dragTheRope(2, input)
fun part2(input: List<String>) = dragTheRope(10, input)

fun dragTheRope(length: Int, input: List<String>): Int {
    val initial = List(length) { Vector(0, 0) }
    val visitedByTail = mutableSetOf<Vector>()

    input.fold(initial) { rope, line ->
        val (dir, distance) = parse(line)

        tailrec fun go(r: List<Vector>, n: Int): List<Vector> =
            if (n == 0) r
            else {
                val newRope = drag(r, dir)
                visitedByTail.add(newRope.last())
                go(newRope, n - 1)
            }

        go(rope, distance)
    }

    return visitedByTail.size
}

fun parse(line: String): Pair<Direction, Int> =
    line.split(" ").let {
        Pair(Direction.valueOf(it[0]), it[1].toInt())
    }

fun drag(rope: List<Vector>, dir: Direction): List<Vector> =
   rope.fold(emptyList()) { acc, nextKnot ->
       if (acc.isEmpty()) listOf(nextKnot + dir.vector)
       else acc + nextKnot.resolve(acc.last())
   }

fun Vector.resolve(head: Vector) =
    if (this.isAdjacentTo(head)) this
    else this + Vector((head.x - x).sign, (head.y - y).sign)
