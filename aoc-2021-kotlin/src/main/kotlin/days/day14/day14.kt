package days.day14

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("day14.txt").string()
    val exampleInput = Reader("day14-example.txt").string()

    time(message = "Part 1", iterations = 1_000, warmUpIterations = 50) {
        solve(input, 10)
    }.checkAnswer(3408L)

    time(message = "Part 2", iterations = 1_000, warmUpIterations = 50) {
        solve(input, 40)
    }.checkAnswer(3724343376942)
}

fun solve(input: String, steps: Int): Long {
    val (template, rules) = parse(input)

    fun go(pairCounts: Map<Pair<Char, Char>, Long>, step: Int): Map<Pair<Char, Char>, Long> {
        if (step == 0) return pairCounts

        val newPairCount = pairCounts.flatMap { (chars, count) ->
            val (char1, char2) = chars
            val insertion = rules[chars]!!
            listOf(
                Pair(char1, insertion) to count,
                Pair(insertion, char2) to count,
            )
        }
            .groupBy(keySelector = { (chars, _) -> chars }, valueTransform = { (_, count) -> count })
            .mapValues { (_, counts) -> counts.sum() }

        return go(newPairCount, step - 1)
    }

    val finalPairCounts = go(countPairs(template), steps)

    return finalPairCounts.toList()
        .groupBy(keySelector = { (chars, _) -> chars.first }, valueTransform = { (_, count) -> count })
        .mapValues { (_, counts) -> counts.sum() }
        .map { (char, count) -> if (char == template.last()) count + 1 else count }
        .let { it.maxOrNull()!! - it.minOrNull()!! }
}

fun countPairs(chars: List<Char>) =
    chars.zipWithNext().groupingBy { it }.eachCount().mapValues { (_, count) -> count.toLong() }

fun parse(input: String): Pair<List<Char>, Map<Pair<Char, Char>, Char>> {
    val (template, rules) = input.split("\n\n")
    val rulesMap = rules.lines().associate {
        it.split(" -> ").let { (from, to) ->
            val foo = from.toList()
            Pair(foo[0], foo[1]) to to.toList().single()
        }
    }
    return Pair(template.toList(), rulesMap)
}
