package days.day13

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day13/input.txt").string()
    val exampleInput = Reader("/day13/example-1.txt").string()

    time(message = "Part 1", warmUp = 500, iterations = 100) {
        part1(input)
    }.checkAnswer(37297)

    time(message = "Part 2", warmUp = 500, iterations = 100) {
        part2(input)
    }.checkAnswer(83197086729371)
}

fun part1(input: String) =
    parseInput(input).mapNotNull { assessMachine(it) }.sum()

fun part2(input: String) =
    parseInput2(input).mapNotNull { assessMachine(it) }.sum()

fun assessMachine(config: Config): Long? {
    val denominator = (config.buttonA.first * config.buttonB.second) - (config.buttonA.second * config.buttonB.first)
    val numerator = (config.buttonB.second * config.prize.first) - (config.buttonB.first * config.prize.second)
    if (numerator % denominator != 0L) return null
    val a = numerator / denominator
    val bNumerator = config.prize.first - (a * config.buttonA.first)
    val bDenominator = config.buttonB.first
    if (bNumerator % bDenominator != 0L) return null
    val b = bNumerator / bDenominator
    return 3 * a + b
}

fun parseInput2(input: String) =
    parseInput(input).map {
        it.copy(prize = Pair(it.prize.first + 10000000000000L, it.prize.second + 10000000000000L))
    }

fun parseInput(input: String): List<Config> {
    val r = """Button A: X\+(\d+), Y\+(\d+)\nButton B: X\+(\d+), Y\+(\d+)\nPrize: X=(\d+), Y=(\d+)""".toRegex()
    return input.split("\n\n").map {
        r.find(it)?.groupValues.let { values ->
            require(values != null)
            Config(
                Pair(values[1].toLong(), values[2].toLong()),
                Pair(values[3].toLong(), values[4].toLong()),
                Pair(values[5].toLong(), values[6].toLong()),
            )
        }
    }
}

data class Config(val buttonA: Pair<Long, Long>, val buttonB: Pair<Long, Long>, val prize: Pair<Long, Long>)
