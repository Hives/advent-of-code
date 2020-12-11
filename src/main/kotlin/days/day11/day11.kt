package days.day11

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day11.txt").strings()

    fun part1(): Int {
        val final = run(input)
        return final.joinToString("").count { it == '#' }
    }

    time("part 1", 100) { part1() }

    fun part2(): Int {
        val final2 = run2(input)
        return final2.joinToString("").count { it == '#' }
    }

    time("part 2", 100) { part2() }
}
