package days.day09

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day09.txt").longs()
    val window = 25

    fun part1(): Long? =
        (window until input.size).toList()
            .find { index ->
                input.subList(index - window, index).findTwoLongsThatAddTo(input[index]).isEmpty()
            }
            ?.let { input[it] }

    val part1 = part1()
    println("part1: $part1")

    fun part2(): Long? {
        return input.findContiguousLongsThatAddTo(part1!!)
            ?.let { (first, last) ->
                val sublist = input.subList(first, last + 1)
                sublist.minOrNull()!! + sublist.maxOrNull()!!
            }
    }

    println("part2: ${part2()}")

    time("part 1") { part1() }
    time("part 2", 100) { part2() }
}