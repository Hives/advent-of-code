package days.day21

import lib.Grid
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day21/input.txt").grid()
    val exampleInput = Reader("/day21/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(3503)

    time(message = "Part 2") {
        part2()
    }.checkAnswer(584211423220706L)
}

fun part1(grid: Grid): Int {
    tailrec fun findReachable(starting: Set<Vector>, steps: Int): Int =
        if (steps == 0) starting.size
        else {
            val next = starting.flatMap { it.neighbours }.filter {
                val space = grid.at(it)
                space != null && space != '#'
            }.toSet()
            findReachable(next, steps - 1)
        }
    return findReachable(setOf(grid.findStart()), 64)
}

fun part2(): Long {
    var steps = 327
    var diff = 57202L
    var count = 89460L

    while (steps < 26_501_365) {
        steps += 131
        diff += 28550
        count += diff
    }

    return count
}

fun Grid.findStart(): Vector {
    indices.forEach { y ->
        this[y].indices.forEach { x ->
            val v = Vector(x, y)
            if (at(v) == 'S') return v
        }
    }
    throw Error("Couldn't find start")
}

fun Grid.at(point: Vector): Char? =
    if (point.x < 0 || point.y < 0 || point.x >= this[0].size || point.y >= this.size) null
    else this[point.y][point.x]
