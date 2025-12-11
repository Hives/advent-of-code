package days.day11

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

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
    return connectionMap.findPaths("you", "out")
}


fun part2(input: List<String>): Long {
    val connectionMap = parseInput(input)

    val fftThenDac = connectionMap.run {
        val svrToFft = findPaths(start = "svr", end = "fft", without = listOf("dac", "out"))
        val fftToDac = findPaths(start = "fft", end = "dac", without = listOf("svr", "out"))
        val dacToOut = findPaths(start = "dac", end = "out", without = listOf("svr", "fft"))
        svrToFft * fftToDac * dacToOut
    }
    val dacThenFft = connectionMap.run {
        val svrToDac = findPaths(start = "svr", end = "dac", without = listOf("ffc", "out"))
        val dacToFft = findPaths(start = "dac", end = "fft", without = listOf("svr", "out"))
        val fftToOut = findPaths(start = "fft", end = "out", without = listOf("svr", "dac"))
        svrToDac * dacToFft * fftToOut
    }

    return fftThenDac + dacThenFft
}

fun Map<String, Set<String>>.findPaths(start: String, end: String, without: List<String> = emptyList()): Long {
    val key = Triple(start, end, without)
    if (end == start) return 1
    if (pathCountMap.containsKey(key)) {
        return pathCountMap[key]!!
    } else {
        val predecessors = this.filter { it.value.contains(end) }.map { it.key }.filterNot { without.contains(it) }
        if (predecessors.isEmpty()) {
            pathCountMap[key] = 0
        } else {
            pathCountMap[key] = predecessors.sumOf { this.findPaths(start, it) }
        }
        return pathCountMap[key]!!
    }
}

val pathCountMap = mutableMapOf<Triple<String, String, List<String>>, Long>()

fun parseInput(input: List<String>): Map<String, Set<String>> =
    input.associate { line -> line.split(": ").let { Pair(it[0], it[1].split(" ").toSet()) } }
