package days.day25

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day25.txt").strings()
    val exampleInput = Reader("day25-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 100, iterations = 1_000) {
        part1(input)
    }.checkAnswer("2-212-2---=00-1--102")
}

fun part1(input: List<String>) =
    input.map(::toDecimal).sum().let(::toSnafu)

fun toDecimal(n: String) =
    n.toList().reversed().fold(Pair(1L, 0L)) { acc, c ->
        val (powerOfFive, runningTotal) = acc
        val digit: Int = when (c) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> throw Exception("Bad input: $c")
        }
        Pair(powerOfFive * 5, runningTotal + (digit * powerOfFive))
    }.second

fun toSnafu(n: Long): String {
    val l = mutableMapOf<Long, Int>()

    fun go(current: Long) {
        if (current == 0L) return
        else {
            val m = powersOfFive.firstNotNullOf { powerOfFive ->
                val remainderRange = (-powerOfFive / 2)..(powerOfFive / 2)
                (-2..2).firstOrNull {
                    (current - (powerOfFive * it)) in remainderRange
                }?.let { Pair(powerOfFive, it) }
            }
            l[m.first] = m.second
            go(current - (m.first * m.second))
        }
    }

    go(n)

    val snafu = powersOfFive.takeWhile { it <= l.keys.max() }.toList().reversed().map {
        l.getOrDefault(it, 0)
    }.map {
        when (it) {
            -2 -> '='
            -1 -> '-'
            0 -> '0'
            1 -> '1'
            2 -> '2'
            else -> throw Exception("uh oh")
        }
    }.joinToString("")

    return snafu
}

val powersOfFive = generateSequence(1L) { it * 5 }
