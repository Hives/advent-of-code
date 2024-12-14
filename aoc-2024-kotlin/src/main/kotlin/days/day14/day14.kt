package days.day14

import kotlin.system.exitProcess
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day14/input.txt").strings()
    val exampleInput = Reader("/day14/example-1.txt").strings()


    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(229980828)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<String>) =
    input.map(::parseInput)
        .map { it.move(100, 101, 103) }
        .countPointsInQuadrants(101, 103)
        .score()

fun part2(input: List<String>): Int {
    return -1
}

fun List<Vector>.countPointsInQuadrants(width: Int, height: Int) =
    listOf(
        Pair(Vector(0, 0), Vector(width / 2, height / 2)),
        Pair(Vector(width / 2 + 1, 0), Vector(width, height / 2)),
        Pair(Vector(0, height / 2 + 1), Vector(width / 2, height)),
        Pair(Vector(width / 2 + 1, height / 2 + 1), Vector(width, height)),
    ).map { quadrant ->
        count { point ->
            point.x >= quadrant.first.x && point.x < quadrant.second.x && point.y >= quadrant.first.y && point.y < quadrant.second.y
        }
    }

fun List<Int>.score() = reduce { acc, i -> i * acc }

fun parseInput(input: String): Robot {
    val r = """^p=(\d+),(\d+) v=(-?\d+),(-?\d+)$""".toRegex()
    return r.find(input)?.groupValues.let { values ->
        require(values != null)
        Robot(
            pos = Vector(values[1].toInt(), values[2].toInt()),
            vel = Vector(values[3].toInt(), values[4].toInt())
        )
    }
}

data class Robot(val pos: Vector, val vel: Vector) {
    fun move(seconds: Int, width: Int, height: Int) =
        (pos + vel * seconds).let { (x, y) -> Vector(x.mod(width), y.mod(height)) }
}
