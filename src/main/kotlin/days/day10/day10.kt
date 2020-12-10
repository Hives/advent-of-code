package days.day10

import lib.Reader
import lib.time

fun main() {
    val example1 = ("16\n" +
            "10\n" +
            "15\n" +
            "5\n" +
            "1\n" +
            "11\n" +
            "7\n" +
            "19\n" +
            "6\n" +
            "12\n" +
            "4\n").trim().lines().map { it.toInt() }

    val example2 = ("28\n" +
            "33\n" +
            "18\n" +
            "42\n" +
            "31\n" +
            "14\n" +
            "46\n" +
            "20\n" +
            "48\n" +
            "47\n" +
            "24\n" +
            "23\n" +
            "49\n" +
            "45\n" +
            "19\n" +
            "38\n" +
            "39\n" +
            "11\n" +
            "1\n" +
            "32\n" +
            "25\n" +
            "35\n" +
            "8\n" +
            "17\n" +
            "7\n" +
            "9\n" +
            "4\n" +
            "2\n" +
            "34\n" +
            "10\n" +
            "3\n").trim().lines().map { it.toInt() }

    val input = Reader("day10.txt").ints()

    fun part1(input: List<Int>): Int {
        val jolts = (input + 0 + (input.maxOrNull()!! + 3)).sorted()
        val a = jolts.zip(jolts.drop(1)).map { it.second - it.first }
        return a.count { it == 1 } * a.count { it == 3 }
    }

    println(part1(input))

    fun part2VeryInefficient(input: List<Int>): Int {
        val device = input.maxOrNull()!! + 3
        val jolts = (input + device).sorted()
        val start = listOf(listOf(0))

        fun List<List<Int>>.foo() = this.partition { it.last() == device }
            .let { (finished, notFinished) ->
                finished + notFinished.flatMap { listOfAdaptors ->
                    jolts.filter { jolt ->
                        val last = listOfAdaptors.last()
                        jolt > last && jolt <= last + 3
                    }
                        .map { listOfAdaptors + it }
                }
            }

        fun iterate(listsOfAdaptors: List<List<Int>>): List<List<Int>> {
            val new = listsOfAdaptors.foo()
            return if (new == listsOfAdaptors) listsOfAdaptors
            else iterate(new)
        }

        return iterate(start).size
    }

    time("part2VeryIneffecient, example2", 100) { part2VeryInefficient(example2) }

    fun part2AlsoInefficient(input: List<Int>): Long {
        val device = input.maxOrNull()!! + 3
        val jolts = (input + 0 + device).sorted()

        val map = jolts.map { start -> start to jolts.filter { end -> end > start && end <= start + 3 } }.toMap()

        fun countPaths(start: Int, end: Int): Long {
            return if (start == end) 1
            else {
                val list = map[start]
                val childPaths = list!!.map { countPaths(it, end) }
                childPaths.sum()
            }
        }

        return countPaths(0, device)
    }

    time("part2AlsoInefficient example2") { part2AlsoInefficient(example2) }

    fun part2(input: List<Int>): Long {
        val wallSocket = 0
        val device = input.maxOrNull()!! + 3
        val jolts = (input + device).sorted()

        val pathsToJolt = mutableMapOf(wallSocket to 1L)

        jolts.forEach { jolt ->
            val pathsToHere = (pathsToJolt[jolt - 1] ?: 0) + (pathsToJolt[jolt - 2] ?: 0) + (pathsToJolt[jolt - 3] ?: 0)
            pathsToJolt[jolt] = pathsToHere
        }

        return pathsToJolt[device] ?: 0
    }

    time("part 2 efficient, example2") { part2(example2) }

    time("part 2", 10_000, 100) { part2(input) }

}

