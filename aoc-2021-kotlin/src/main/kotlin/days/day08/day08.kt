package days.day08

import lib.Reader

fun main() {
    val input = Reader("day08.txt").strings()
    val exampleInput = Reader("day08-example.txt").strings()

    println(part1(input))
    println(part2(input))
}

fun part1(strings: List<String>): Int {
    val input = strings.map(::parseInput)

    return input.sumOf { (tenSignalPatterns, fourDigits) ->
        val mappy = foo(tenSignalPatterns)
        fourDigits.map { mappy[it] }.count { it == 1 || it == 4 || it == 7 || it == 8 }
    }
}

fun part2(strings: List<String>): Int {
    val input = strings.map(::parseInput)

    return input.sumOf { (tenSignalPatterns, fourDigits) ->
        val mappy = foo(tenSignalPatterns)
        println(mappy)
        digitsToNumber(fourDigits.map { mappy[it]!! })
    }
}

fun digitsToNumber(digits: List<Int>) = digits[3] + (10 * digits[2]) + (100 * digits[1]) + (1_000 * digits[0])

fun foo(patterns: List<Set<Char>>): Map<Set<Char>, Int> {
    val map = mutableMapOf<Set<Char>, Int>()

    val segmentCountMap = patterns.groupBy { it.size }

    val onePattern = segmentCountMap[2]!!.single()
    val sevenPattern = segmentCountMap[3]!!.single()
    val fourPattern = segmentCountMap[4]!!.single()
    val threePattern = segmentCountMap[5]!!.single { (onePattern - it).isEmpty() }
    val ninePattern = segmentCountMap[6]!!.single { (it - threePattern).size == 1 }
    val twoPattern = segmentCountMap[5]!!.single { (it - ninePattern).size == 1 }
    val fivePattern = segmentCountMap[5]!!.single { (it - ninePattern).isEmpty() && it != threePattern }
    val eightPattern = segmentCountMap[7]!!.single()
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