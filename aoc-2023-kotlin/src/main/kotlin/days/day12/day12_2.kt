package days.day12_2

import lib.Reader
import lib.checkAnswer
import lib.time
import java.math.BigInteger
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day12/input.txt").strings()
    val exampleInput = Reader("/day12/example-1.txt").strings()

    println(part2(input))

    exitProcess(0)

    time(message = "Part 1", warmUpIterations = 5, iterations = 5) {
        part1(input)
    }.checkAnswer(8075)

    time(message = "Part 2") {
        part2(exampleInput)
    }.checkAnswer(0)
}

fun part1(input: List<String>): Long =
    input.map(::parse).sumOf(::solveRow)

fun part2(input: List<String>): Long {
    val rows = input.map(::parse2)

    // input index 7 is pretty slow
    // input index 20 is slower

    solveRow(rows[20])

    return -1
}

fun solveRow(row: Row): Long {
    val (condition, groups) = row
    val arrangementsGetter = ArrangementsGetter()
    return arrangementsGetter.getArrangements(condition, groups).count().toLong()
}

fun createLoggy(depth: Int) =
    fun(s: Any) {
        println("${createString(depth * 3, '-')}> $s")
    }

class ArrangementsGetter {
    val memo = mutableMapOf<Pair<String, List<Int>>, List<String>>()

    fun getArrangementsMemoized(condition: String, groups: List<Int>, depth: Int = 0): List<String> {
        println("memo.keys.size: ${memo.keys.size}")
        println("memo.values.sumOf { it.count() }: ${memo.values.sumOf { it.count() }}")
        val key = Pair(condition, groups)
        val memoized = memo[key]
        return if (memoized != null) {
            memoized
        } else {
            val result = getArrangements(condition, groups, depth)
            memo[key] = result
            result
        }
    }

    fun getArrangements(condition: String, groups: List<Int>, depth: Int = 0): List<String> {
        val loggy = createLoggy(depth)

        loggy("$condition $groups")

        if (groups.isEmpty()) {
            loggy("groups is empty")
            val dots = createString(condition.length, '.')
            return if (dots.matches(condition)) {
                loggy("returning dots")
                listOf(dots)
            } else {
                loggy("dots don't match, returning nothing")
                emptyList()
            }
        } else {
            val firstGroup = groups[0]
            val remainingGroups = groups.drop(1)

            loggy("length: ${condition.length}")

            val minimumSpaceRequiredForRemainingGroups =
                if (remainingGroups.isEmpty()) 0
                else remainingGroups.sum() + remainingGroups.count() - 1

            loggy("minimumSpaceRequiredForRemainingGroups: $minimumSpaceRequiredForRemainingGroups")

            val spaceAvailableForFirstGroup =
                if (remainingGroups.isEmpty()) condition.length - minimumSpaceRequiredForRemainingGroups
                else condition.length - minimumSpaceRequiredForRemainingGroups - 1

            loggy("spaceAvailableForFirstGroup: $spaceAvailableForFirstGroup")

            val maxInitialPadding = spaceAvailableForFirstGroup - firstGroup
            val possibleFirstSections = (0..maxInitialPadding).map { padding ->
                createString(padding, '.') + createString(firstGroup, '#') +
                        if (remainingGroups.isEmpty()) "" else "."
            }

            loggy("---")
            loggy(possibleFirstSections)

            val validFirstSections =
                possibleFirstSections.filter { it.matches(condition) }

            loggy("---")
            loggy(validFirstSections)

            return validFirstSections.flatMap { section ->
                loggy("recursing once for $section")
                getArrangements(condition.drop(section.length), remainingGroups, depth + 1)
                    .map { "$section$it" }
            }
        }
    }
}

fun totalPossibilities(row: Row): BigInteger {
    // total number of arrangements ignoring the condition contents
    val (condition, groups) = row
    val hashCount = groups.sum()
    val dotCount = condition.length - hashCount
    val requiredGaps = groups.size - 1
    val remainingDots = dotCount - requiredGaps
    val dotTargetSlots = groups.size + 1

    return binomial(remainingDots + dotTargetSlots - 1, dotTargetSlots - 1)
}

fun binomial(n: Int, k: Int): BigInteger {
    val numerator = Fact.factorial(n)
    val denominator = Fact.factorial(k).multiply(Fact.factorial(n - k))
    return numerator.divide(denominator)
}

object Fact {
    fun factorial(n: Int): BigInteger {
        return if (n in facts) facts[n]!! else {
            val f = BigInteger.valueOf(n.toLong()).multiply(factorial(n - 1))
            facts[n] = f
            return f
        }
    }

    private val facts = mutableMapOf<Int, BigInteger>(0 to BigInteger.ONE)
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
