package days.day13

import kotlin.system.exitProcess
import lib.Reader
import lib.Vector
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day13/input.txt").string()
    val exampleInput = Reader("/day13/example-1.txt").string()

    part1(input).checkAnswer(37297)
//    part2(input)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(37297)

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: String) =
    parseInput(input).map { assessMachine(it, 100) }.sumOf { it ?: 0 }

fun part2(input: String): Int {
    parseInput(input).map { assessMachine2(it) }
    return -1
}

fun assessMachine2(config: Config) {
}

fun assessMachine(config: Config, max: Int): Int? {
    return (1..max).flatMap { aPresses ->
        (1..max).mapNotNull { bPresses ->
            val final = (config.buttonA * aPresses) + (config.buttonB * bPresses)
            if (final == config.prize) Pair(aPresses, bPresses)
            else null
        }
    }.minOfOrNull { it.price() }
}

fun getMin(config: Config, max: Int): Pair<Int, Int>? {
    return (1..max).flatMap { aPresses ->
        (1..max).mapNotNull { bPresses ->
            val final = (config.buttonA * aPresses) + (config.buttonB * bPresses)
            if (final == config.prize) Pair(aPresses, bPresses)
            else null
        }
    }.minByOrNull { it.price() }
}

fun Pair<Int, Int>?.price() =
    if (this == null) 0 else 3 * first + second

fun parseInput(input: String): List<Config> {
    val r = """Button A: X\+(\d+), Y\+(\d+)\nButton B: X\+(\d+), Y\+(\d+)\nPrize: X=(\d+), Y=(\d+)""".toRegex()
    return input.split("\n\n").map {
        r.find(it)?.groupValues.let { values ->
            require(values != null)
            Config(
                Vector(values[1].toInt(), values[2].toInt()),
                Vector(values[3].toInt(), values[4].toInt()),
                Vector(values[5].toInt(), values[6].toInt()),
            )
        }
    }
}

data class Config(val buttonA: Vector, val buttonB: Vector, val prize: Vector)
