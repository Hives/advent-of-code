package days.day12

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day12/input.txt").strings()
    val exampleInput = Reader("/day12/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(8075)

    time(message = "Part 2", warmUpIterations = 2, iterations = 2) {
        part2(input)
    }.checkAnswer(4232520187524)
}

fun part1(input: List<String>): Long =
    input.map(::parse).sumOf(::solveRow)

fun part2(input: List<String>): Long =
    input.map(::parse2).sumOf(::solveRow)

fun solveRow(row: Row): Long {
    val (condition, groups) = row
    return ArrangementsCounter().countArrangementsMemoized(condition, groups)
}

class ArrangementsCounter {
    fun countArrangementsMemoized(condition: String, groups: List<Int>): Long {
        val key = Pair(condition, groups)
        val memoized = memo[key]
        return if (memoized != null) {
            memoized
        } else {
            val result = countArrangements(condition, groups)
            memo[key] = result
            result
        }
    }

    private fun countArrangements(condition: String, groups: List<Int>): Long {
        if (groups.isEmpty()) {
            val dots = createString(condition.length, '.')
            return if (dots.matches(condition)) 1 else 0
        } else {
            val firstGroup = groups[0]
            val remainingGroups = groups.drop(1)

            val minimumSpaceRequiredForRemainingGroups =
                if (remainingGroups.isEmpty()) 0
                else remainingGroups.sum() + remainingGroups.count() - 1

            val spaceAvailableForFirstGroup =
                if (remainingGroups.isEmpty()) condition.length - minimumSpaceRequiredForRemainingGroups
                else condition.length - minimumSpaceRequiredForRemainingGroups - 1

            val maxInitialPadding = spaceAvailableForFirstGroup - firstGroup
            val possibleFirstSections = (0..maxInitialPadding).map { padding ->
                createString(padding, '.') + createString(firstGroup, '#') +
                        if (remainingGroups.isEmpty()) "" else "."
            }

            val validFirstSections =
                possibleFirstSections.filter { it.matches(condition) }

            return validFirstSections.sumOf { section ->
                countArrangementsMemoized(condition.drop(section.length), remainingGroups)
            }
        }
    }

    private val memo = mutableMapOf<Pair<String, List<Int>>, Long>()

}

fun String.matches(other: String) =
    zip(other).all { if (it.second == '?') true else it.first == it.second }

fun createString(length: Int, char: Char) =
    List(length) { char }.joinToString("")

fun parse(input: String): Row =
    input.split(' ').let { (first, second) ->
        val ns = second.split(',').map(String::toInt)
        return Pair(first, ns)
    }

fun parse2(input: String): Row =
    parse(input).let { (condition, groups) ->
        Pair(
            List(5) { condition }.joinToString("?"),
            List(5) { groups }.flatten()
        )
    }

typealias Row = Pair<String, List<Int>>
