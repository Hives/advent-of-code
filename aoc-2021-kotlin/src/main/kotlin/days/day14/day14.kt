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

    tailrec fun go(pairCount: Map<TwoChars, Long>, step: Int): Map<TwoChars, Long> {
        if (step == 0) return pairCount

        val newPairCount = pairCount.flatMap { (twoChars, count) ->
            val (char1, char2) = twoChars
            val insertion = rules[twoChars]!!
            listOf(
                Pair(char1, insertion) to count,
                Pair(insertion, char2) to count,
            )
        }
            .groupByAndReduce(
                groupBy = { (twoChars, _) -> twoChars },
                reduce = { it.sumOf { (_, count) -> count } }
            )

        return go(newPairCount, step - 1)
    }

    val finalPairCounts = go(countPairs(template), steps)

    return finalPairCounts.toList()
        .groupByAndReduce(
            groupBy = { (twoChars, _) -> twoChars.first },
            reduce = { it.sumOf { (_, count) -> count } }
        )
        .map { (char, count) -> if (char == template.last()) count + 1 else count }
        .let { it.maxOrNull()!! - it.minOrNull()!! }
}

fun <T, K, V> List<T>.groupByAndReduce(
    groupBy: (T) -> K,
    reduce: (List<T>) -> V,
): Map<K, V> =
    groupBy(keySelector = groupBy)
        .mapValues { (_, values) -> reduce(values) }

fun countPairs(chars: List<Char>) =
    chars.zipWithNext().groupingBy { it }.eachCount().mapValues { (_, count) -> count.toLong() }

fun parse(input: String): Pair<List<Char>, Map<TwoChars, Char>> {
    val (template, rules) = input.split("\n\n")
    val rulesMap = rules.lines().associate {
        it.split(" -> ").let { (from, to) ->
            val foo = from.toList()
            Pair(foo[0], foo[1]) to to.toList().single()
        }
    }
    return Pair(template.toList(), rulesMap)
}

typealias TwoChars = Pair<Char, Char>
