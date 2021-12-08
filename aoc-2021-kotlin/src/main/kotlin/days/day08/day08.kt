package days.day08

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day08.txt").strings()
    val exampleInput = Reader("day08-example.txt").strings()

    time(iterations = 1_000, warmUpIterations = 100, message = "Part 1") {
        part1(input)
    }.checkAnswer(381)

    time(iterations = 1_000, warmUpIterations = 100, message = "Part 2") {
        part2(input)
    }.checkAnswer(1023686)
}

fun part1(lines: List<String>): Int {
    val input = lines.map(::parseInput)

    return input.sumOf { (tenSignalPatterns, fourDigits) ->
        val patternMap = createPatternMap(tenSignalPatterns)
        fourDigits.map { patternMap[it] }.count { it == 1 || it == 4 || it == 7 || it == 8 }
    }
}

fun part2(lines: List<String>): Int {
    val input = lines.map(::parseInput)

    return input.sumOf { (tenSignalPatterns, fourDigits) ->
        val patternMap = createPatternMap(tenSignalPatterns)
        fourDigits.map { patternMap[it]!! }.let(::digitsToNumber)
    }
}

fun createPatternMap(patterns: List<Set<Char>>): Map<Set<Char>, Int> {
    val segmentCountMap = patterns.groupBy { it.size }

    val onePattern = segmentCountMap[2]!!.single()
    val sevenPattern = segmentCountMap[3]!!.single()
    val fourPattern = segmentCountMap[4]!!.single()
    val eightPattern = segmentCountMap[7]!!.single()
    val threePattern = segmentCountMap[5]!!.single { it.containsAll(onePattern) }
    val ninePattern = segmentCountMap[6]!!.single { (it - threePattern).size == 1 }
    val fivePattern = segmentCountMap[5]!!.single { (it - ninePattern).isEmpty() && it != threePattern }
    val twoPattern = segmentCountMap[5]!!.single { (it - ninePattern).size == 1 }
    val zeroPattern = segmentCountMap[6]!!.single { (it - fivePattern).size == 2 }
    val sixPattern = segmentCountMap[6]!!.single { it !== zeroPattern && it !== ninePattern }

    return mapOf(
        zeroPattern to 0,
        onePattern to 1,
        twoPattern to 2,
        threePattern to 3,
        fourPattern to 4,
        fivePattern to 5,
        sixPattern to 6,
        sevenPattern to 7,
        eightPattern to 8,
        ninePattern to 9
    )
}

fun digitsToNumber(digits: List<Int>) = digits[3] + (10 * digits[2]) + (100 * digits[1]) + (1_000 * digits[0])

fun parseInput(line: String): Pair<List<Set<Char>>, List<Set<Char>>> {
    val splitty = { string: String ->
        string.split(" ").map { it.toCharArray().toSet() }
    }

    return line.split(" | ")
        .let { (first, second) ->
            val tenUniqueSignalPatterns = splitty(first)
            val fourDigitOutputValue = splitty(second)
            Pair(tenUniqueSignalPatterns, fourDigitOutputValue)
        }
}