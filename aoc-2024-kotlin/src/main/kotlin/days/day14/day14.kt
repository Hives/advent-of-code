package days.day14

import kotlin.system.exitProcess
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day14/input.txt").strings()
    val exampleInput = Reader("/day14/example-1.txt").strings()

    part2(input)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(229980828)

    time(message = "Part 2", iterations = 5, warmUp = 5) {
        part2(input)
    }.checkAnswer(7132)
}

fun part1(input: List<String>) =
    input.map(::parseInput)
        .map { it.move(100, 101, 103) }
        .countRobotsInQuadrants(101, 103)
        .score()

fun part2(input: List<String>): Int {
    var robots = input.map(::parseInput)
    var seconds = 0
    while (!robots.hasDiagonals(101)) {
        seconds ++
        robots = robots.map { it.move(1, 101, 103) }
    }
    robots.printy(101, 103)
    return seconds
}

fun List<Robot>.countRobotsInQuadrants(width: Int, height: Int) =
    listOf(
        Pair(Vector(0, 0), Vector(width / 2, height / 2)),
        Pair(Vector(width / 2 + 1, 0), Vector(width, height / 2)),
        Pair(Vector(0, height / 2 + 1), Vector(width / 2, height)),
        Pair(Vector(width / 2 + 1, height / 2 + 1), Vector(width, height)),
    ).map { quadrant ->
        count { (point) ->
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

fun List<Robot>.hasDiagonals(width: Int): Boolean {
    val centered = filter { it.pos.x == width / 2 + 1 }
    val points = map(Robot::pos).toSet()
    return centered.any {
        List(5) { n -> it.pos + (Vector(-1, 1) * n) }.all { it in points }
    }
}

fun List<Robot>.printy(width: Int, height: Int) {
    val points = map(Robot::pos).toSet()
    (0..height).forEach { y ->
        (0..width).forEach { x ->
            if (Vector(x, y) in points) print("X")
            else print(".")
        }
        println()
    }
    println()
}

data class Robot(val pos: Vector, val vel: Vector) {
    fun move(seconds: Int, width: Int, height: Int): Robot =
        (pos + vel * seconds).let { (x, y) ->
            copy(pos = Vector(x.mod(width), y.mod(height)))
        }
}
