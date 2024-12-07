package days.day07

import days.day07.Operator.PLUS
import days.day07.Operator.TIMES
import lib.Reader
import lib.checkAnswer
import lib.time
import lib.toLongs

fun main() {
    val input = Reader("/day07/input.txt").strings()
    val exampleInput = Reader("/day07/example-1.txt").strings()

    time(message = "Part 1", iterations = 10, warmUp = 10) {
        part1(input)
    }.checkAnswer(4364915411363)

    time(message = "Part 2", iterations = 1, warmUp = 0) {
        part2(input)
    }.checkAnswer(38322057216320)
}

fun part1(input: List<String>) =
    findResolvableEquations(input.map(::parseInput), listOf(PLUS, TIMES))
        .sumOf { (total) -> total }

fun part2(input: List<String>): Long {
    val equations = input.map(::parseInput)
    val canBeMadeWithPlusOrTimes = findResolvableEquations(equations, listOf(PLUS, TIMES))

    val theRest = equations.filterNot { canBeMadeWithPlusOrTimes.contains(it) }

    return canBeMadeWithPlusOrTimes.sumOf { it.first } +
            findResolvableEquations(theRest, Operator.entries).sumOf { it.first }
}

fun findResolvableEquations(
    equations: List<Equation>,
    allowedOperators: List<Operator>
): List<Pair<Long, List<Long>>> =
    equations.filter { (total, ns) ->
        val operatorsList = operatorPermutations(ns.size, allowedOperators)
        operatorsList.any { operators ->
            total == ns.drop(1).zip(operators).fold(ns[0]) { acc, (n, plusOrTimes) ->
                plusOrTimes.f(acc, n)
            }
        }
    }

enum class Operator(val f: (Long, Long) -> Long) {
    PLUS({ a, b -> a + b }),
    TIMES({ a, b -> a * b }),
    CONCATENATE({ a, b -> (a.toString() + b.toString()).toLong() })
}

fun operatorPermutations(n: Int, allowedOperators: List<Operator>): List<List<Operator>> {
    tailrec fun go(n: Int, perms: List<List<Operator>>): List<List<Operator>> {
        if (n == 0) return perms

        return go(
            n - 1,
            allowedOperators.flatMap { operator -> perms.map { it + operator } }
        )
    }

    return go(n - 1, listOf(emptyList()))
}

typealias Equation = Pair<Long, List<Long>>

fun parseInput(input: String): Equation =
    input.split(": ")
        .let { Pair(it[0].toLong(), it[1].toLongs()) }
