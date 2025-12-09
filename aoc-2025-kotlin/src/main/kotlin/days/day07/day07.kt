package days.day07

import lib.Grid
import lib.Reader
import lib.Vector
import lib.at
import lib.cells
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day07/input.txt").grid()
    val (part1, part2) = Reader("/day07/answers.txt").longs()
    val exampleInput = Reader("/day07/example-1.txt").grid()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: Grid<Char>): Long {
    tailrec fun go(front: Set<Vector>, splitCount: Long = 0L): Long {
        return if (front.size == 0) splitCount
        else {
            var additionalSplits = 0
            val newFront = front.toList().mapNotNull { v ->
                if (v.y.toInt() == input.size - 1) null
                else {
                    val v2 = v + Vector(0, 1)
                    val c = input.at(v2, '.')
                    if (c == '.') listOf(v2)
                    else {
                        additionalSplits += 1
                        listOf(v2 + Vector(1, 0), v2 + Vector(-1, 0))
                    }
                }
            }.flatten().toSet()
            go(newFront, splitCount + additionalSplits)
        }
    }
    return go(setOf(input.cells().first { it.second == 'S' }.first))
}

fun part2(input: Grid<Char>): Long {
    tailrec fun go(front: List<Pair<Long, Vector>>): Long {
        val newFront = front.mapNotNull { (timelines, v) ->
            if (v.y >= input.size - 1) null
            else {
                val v2 = v + Vector(0, 1)
                val c = input.at(v2, '.')
                if (c == '.') listOf(Pair(timelines, v2))
                else {
                    listOf(
                        Pair(timelines, v2 + Vector(1, 0)),
                        Pair(timelines, v2 + Vector(-1, 0))
                    )
                }
            }
        }.flatten().groupBy { it.second }.map { (v, pairs) ->
            Pair(pairs.sumOf { it.first }, v)
        }

        return if (newFront.isEmpty()) front.sumOf { it.first }
        else go(newFront)
    }

    val start = input.cells().first { it.second == 'S' }.first
    val front = listOf(Pair(1L, start))
    return go(front)
}
