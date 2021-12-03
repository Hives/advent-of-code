package days.day03

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day03.txt").strings()

    time(iterations = 10_000, warmUpIterations = 100) {
        part1(input)
    }.checkAnswer(2648450)

    time(iterations = 10_000, warmUpIterations = 100) {
        part2(input)
    }.checkAnswer(2845944)
}

fun part1(input: List<String>): Int =
    (0 until input[0].length).map { index ->
        input.map { it[index] }
            .fold(DigitCount()) { digits, next ->
                if (next == '0') digits.inc0() else digits.inc1()
            }
            .let { digits ->
                if (digits.zeros > digits.ones) '0' else '1'
            }
    }
        .joinToString("")
        .let { Pair(it, it.invert()) }
        .let { (gamma, epsilon) -> gamma.toInt(2) * epsilon.toInt(2) }

fun part2(input: List<String>): Int =
    getOxygenGeneratorRating(input) * getC02ScrubberRating(input)

fun getC02ScrubberRating(input: List<String>): Int =
    filterBySuccessiveIndexes(input, ::selectLeastCommonDigitAtIndex).toInt(2)

fun getOxygenGeneratorRating(input: List<String>): Int =
    filterBySuccessiveIndexes(input, ::selectMostCommonDigitAtIndex).toInt(2)

fun filterBySuccessiveIndexes(input: List<String>, filter: (List<String>, Int) -> List<String>): String {
    tailrec fun go(input: List<String>, index: Int): String {
        val filtered = filter(input, index)
        return filtered.singleOrNull() ?: go(filtered, index + 1)
    }
    return go(input, 0)
}

fun selectLeastCommonDigitAtIndex(input: List<String>, index: Int): List<String> =
    countDigitsAtIndexAndSelect(input, index) { digits ->
        when {
            (digits.zeros > digits.ones) -> '0'
            (digits.ones > digits.zeros) -> '1'
            else -> '1'
        }
    }

fun selectMostCommonDigitAtIndex(input: List<String>, index: Int): List<String> =
    countDigitsAtIndexAndSelect(input, index) { digits ->
        when {
            (digits.zeros > digits.ones) -> '1'
            (digits.ones > digits.zeros) -> '0'
            else -> '0'
        }
    }

fun countDigitsAtIndexAndSelect(
    input: List<String>,
    index: Int,
    selectByDigitCount: (DigitCount) -> Char
): List<String> {
    val digitToSelectBy = input.map { it[index] }.fold(DigitCount()) { digits, next ->
        if (next == '0') digits.inc0() else digits.inc1()
    }
        .let { selectByDigitCount(it) }

    return input.filter { it[index] == digitToSelectBy }
}

data class DigitCount(val zeros: Int = 0, val ones: Int = 0) {
    fun inc0() = this.copy(zeros = zeros + 1)
    fun inc1() = this.copy(ones = ones + 1)
}

fun String.invert() = this.map { if (it == '0') '1' else '0' }.joinToString("")

private val testInput = ("00100\n" +
        "11110\n" +
        "10110\n" +
        "10111\n" +
        "10101\n" +
        "01111\n" +
        "00111\n" +
        "11100\n" +
        "10000\n" +
        "11001\n" +
        "00010\n" +
        "01010").lines()
