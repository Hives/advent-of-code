package days.day10

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.abs

fun main() {
    val input = Reader("day10.txt").strings()
    val exampleInput = Reader("day10-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 100, iterations = 5000) {
        part1(input)
    }.checkAnswer(15260)

    time(message = "Part 2", warmUpIterations = 100, iterations = 5000) {
        part2(input)
    }.checkAnswer(
        """
        ###...##..#..#.####..##..#....#..#..##..
        #..#.#..#.#..#.#....#..#.#....#..#.#..#.
        #..#.#....####.###..#....#....#..#.#....
        ###..#.##.#..#.#....#.##.#....#..#.#.##.
        #....#..#.#..#.#....#..#.#....#..#.#..#.
        #.....###.#..#.#.....###.####..##...###.
    """.trimIndent())
}

fun part1(input: List<String>): Int {
    val cycleMap = generateCycleMap(input)
    val requestedCycles = listOf(20, 60, 100, 140, 180, 220)
    return requestedCycles.sumOf { c -> cycleMap[c]!! * c }
}

fun part2(input: List<String>): String {
    val cycleMap = generateCycleMap(input)
    return List(6) { it }.map { row ->
        List(40) { col ->
            val cycle = 40 * row + col + 1
            Pair(cycle, col)
        }
    }
        .map {
            it.map { (cycle, position) ->
                if (abs(cycleMap[cycle]!! - position) < 2) '#' else '.'
            }.joinToString("")
        }.joinToString("\n")
}

fun generateCycleMap(input: List<String>): Map<Int, Int> {
    val cycleMap = mutableMapOf(1 to 1)
    input.fold(1) { cycle, line ->
        if (line.startsWith("noop")) {
            cycleMap[cycle + 1] = cycleMap[cycle]!!
            cycle + 1
        } else {
            cycleMap[cycle + 1] = cycleMap[cycle]!!
            cycleMap[cycle + 2] = cycleMap[cycle + 1]!! + line.split(" ")[1].toInt()
            cycle + 2
        }
    }
    return cycleMap
}
