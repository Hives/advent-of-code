package days.day05

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day05/input.txt").string()
    val exampleInput = Reader("/day05/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(6267)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(5184)
}

fun part1(input: String): Int {
    val (rules, pageSequences) = parseInput(input)
    return pageSequences.filter { pageSequence ->
        rules.all { rule -> validateOrdering(pageSequence, rule) }
    }.sumOf(List<Int>::middle)
}

fun part2(input: String): Int {
    val (allRules, pageSequences) = parseInput(input)
    val incorrectlyOrdered = pageSequences.filter { pageSequence ->
        allRules.any { rule -> !validateOrdering(pageSequence, rule) }
    }
    return incorrectlyOrdered.map { pageSequence ->
        val applicableRules = allRules.filter { rule ->
            pageSequence.contains(rule.first) && pageSequence.contains(rule.second)
        }
        pageSequence.indices.map { idx ->
            pageSequence.find { pageNumber ->
                applicableRules.count { it.first == pageNumber } == pageSequence.size - idx - 1
            } ?: throw Exception("Oh no")
        }
    }.sumOf(List<Int>::middle)
}

fun validateOrdering(ordering: List<Int>, rule: Pair<Int, Int>): Boolean {
    val i1 = ordering.indexOf(rule.first)
    val i2 = ordering.indexOf(rule.second)
    return if (i1 == -1 || i2 == -1) {
        true
    } else {
        i1 < i2
    }
}

fun List<Int>.middle() = get(size / 2)

fun parseInput(input: String): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
    val (s1, s2) = input.split("\n\n")
    return Pair(
        s1.split("\n").map { it.split("|").map(String::toInt).let { (n1, n2) -> Pair(n1, n2) } },
        s2.split("\n").map { it.split(",").map(String::toInt) }
    )
}
