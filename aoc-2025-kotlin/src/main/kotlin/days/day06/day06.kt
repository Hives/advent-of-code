package days.day06

import lib.Reader
import lib.checkAnswer
import lib.flip
import lib.time

fun main() {
    val input = Reader("/day06/input.txt").strings()
    val (part1, part2) = Reader("/day06/answers.txt").longs()
    val exampleInput = Reader("/day06/example-1.txt").strings()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>): Long {
    val spaces = " +".toRegex()
    val operations = input.last().split(spaces).filterNot { it.isEmpty() }
    val numbers = input.dropLast(1).map {
        it.split(spaces).filterNot(String::isEmpty).map(String::toLong)
    }
    return operations.indices.fold(0L) { acc, n ->
        acc + numbers.map { it[n] }.reduce { acc, number ->
            if (operations[n] == "+") acc + number
            else acc * number
        }
    }
}

fun part2(input: List<String>): Long =
    input.map(String::toList)
        .flip()
        .split { it.all { c -> c == ' ' } }
        .sumOf { chunk ->
            val operation = chunk.first().last()
            val numbers = chunk.map { it.dropLast(1).joinToString("").trim().toLong() }
            numbers.reduce { acc, n ->
                if (operation == '*') acc * n else acc + n
            }
        }

fun <T> List<T>.split(test: (T) -> Boolean): List<List<T>> =
    this.fold(Pair(emptyList<List<T>>(), emptyList<T>())) { (acc, chunk), t ->
        if (test(t)) Pair(acc + listOf(chunk), emptyList())
        else (Pair(acc, chunk + t))
    }.let { (a, b) -> a + listOf(b) }

