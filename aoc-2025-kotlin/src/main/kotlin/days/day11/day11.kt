package days.day11

import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day11/input.txt").strings()
    val (part1, part2) = Reader("/day11/answers.txt").longs()
    val exampleInput = Reader("/day11/example-1.txt").strings()
    val exampleInput2 = Reader("/day11/example-2.txt").strings()

    time(warmUp = 10000, iterations = 100, message = "Part 1") {
        part1(input)
    }.checkAnswer(part1)

    time(warmUp = 10000, iterations = 100, message = "Part 2") {
        part2(input)
    }.checkAnswer(part2)
}

fun part1(input: List<String>): Long {
    val connectionMap = parseInput(input)
    return connectionMap.findPaths(start = "you", end = "out")
}


fun part2(input: List<String>): Long {
    val connectionMap = parseInput(input)

    val fftThenDac = connectionMap.run {
        val svrToFft = findPaths(start = "svr", end = "fft")
        val fftToDac = findPaths(start = "fft", end = "dac")
        val dacToOut = findPaths(start = "dac", end = "out")
        svrToFft * fftToDac * dacToOut
    }
    val dacThenFft = connectionMap.run {
        val svrToDac = findPaths(start = "svr", end = "dac")
        val dacToFft = findPaths(start = "dac", end = "fft")
        val fftToOut = findPaths(start = "fft", end = "out")
        svrToDac * dacToFft * fftToOut
    }

    return fftThenDac + dacThenFft
}

fun Map<String, Set<String>>.findPaths(start: String, end: String): Long {
    val key = Pair(start, end)

    return when {
        end == start -> 1
        else -> {
            if (!pathCountMap.containsKey(key)) {
                val predecessors = this.filter { (_, to) -> to.contains(end) }.map { (from, _) -> from }

                if (predecessors.isEmpty()) {
                    pathCountMap[key] = 0
                } else {
                    pathCountMap[key] = predecessors.sumOf { predecessor ->
                        this.findPaths(start, predecessor)
                    }
                }
            }

            pathCountMap[key]!!
        }
    }
}

val pathCountMap = mutableMapOf<Pair<String, String>, Long>()

fun parseInput(input: List<String>): Map<String, Set<String>> =
    input.associate { line -> line.split(": ").let { Pair(it[0], it[1].split(" ").toSet()) } }
