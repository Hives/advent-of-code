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

fun solve(input: String, totalSteps: Int): Long {
    val (template, rules) = parse(input)

    fun go(countMap: Map<Pair<Char, Char>, Long>, steps: Int): Map<Pair<Char, Char>, Long> {
        if (steps == 0) return countMap

        val newCountMap = mutableMapOf<Pair<Char, Char>, Long>()

        countMap.forEach { (chars, count) ->
            val insertion = rules[chars]!!
            val pair1 = Pair(chars.first, insertion)
            val pair2 = Pair(insertion, chars.second)
            newCountMap[pair1] = (newCountMap[pair1] ?: 0).plus(count)
            newCountMap[pair2] = (newCountMap[pair2] ?: 0).plus(count)
        }

        return go(newCountMap, steps - 1)
    }

    val initialCountMap =
        template.zipWithNext().groupingBy { it }.eachCount().mapValues { (_, count) -> count.toLong() }

    val finalCountMap = go(initialCountMap, totalSteps)

    return finalCountMap.toList().map { (chars, count) ->
        chars.first to count
    }
        .groupBy { it.first }
        .map { (char, listy) ->
            char to listy.sumOf { it.second }
        }
        .map { (char, count) ->
            if (char == template.last()) count + 1 else count
        }
        .let {
            it.maxOrNull()!! - it.minOrNull()!!
        }
}

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
