package days.day10

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.abs

fun main() {
    val input = Reader("day10.txt").strings()
    val exampleInput = Reader("day10-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 10_000, iterations = 1_000) {
        part1(input)
    }.checkAnswer(15260)

    time(message = "Part 2", warmUpIterations = 10_000, iterations = 1_000) {
        part2(input)
    }.checkAnswer(
        """
        ███...██..█..█.████..██..█....█..█..██..
        █..█.█..█.█..█.█....█..█.█....█..█.█..█.
        █..█.█....████.███..█....█....█..█.█....
        ███..█.██.█..█.█....█.██.█....█..█.█.██.
        █....█..█.█..█.█....█..█.█....█..█.█..█.
        █.....███.█..█.█.....███.████..██...███.
        """.trimIndent()
    )
}

fun part1(input: List<String>): Int {
    val registerValues = generateRegisterValues(input)
    val requestedCycles = listOf(20, 60, 100, 140, 180, 220)
    return requestedCycles.sumOf { c -> registerValues[c] * c }
}

fun part2(input: List<String>): String {
    val registerValues = generateRegisterValues(input)
    return (1..240).map { cycle ->
        val xPosition = (cycle - 1) % 40
        if (abs(registerValues[cycle] - xPosition) < 2) "█" else "."
    }.chunked(40).joinToString("\n") { it.joinToString("") }
}

fun generateRegisterValues(input: List<String>): List<Int> =
    input.fold(listOf(1, 1)) { registerValues, line ->
        val last = registerValues.last()
        if (line.startsWith("noop")) {
            registerValues + last
        } else {
            val x = line.split(" ").last().toInt()
            registerValues + last + (last + x)
        }
    }
