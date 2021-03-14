package days.day15

import lib.time

val puzzleInput = listOf(9, 6, 0, 10, 18, 2, 1)

fun main() {

    time("part 1") {
        val part1 = CountingGame(puzzleInput)
        part1.go(2020)
        // should equal 1238
        part1.current
    }

    // warning - slow (~6s per iteration)
    time("part 2", 10, 10) {
        val part2 = CountingGame(puzzleInput)
        part2.go(30_000_000)
        // should equal 3745954
        part2.current
    }

}

class CountingGame(startingNumbers: List<Int>) {
    private val lastTimeNumberWasCalled =
        startingNumbers.subList(0, startingNumbers.size - 1).mapIndexed { index, i -> i to index }
            .toMap().toMutableMap()
    private var position = startingNumbers.size - 1
    var current = startingNumbers.last()

    fun go(n: Int) {
        while (position < n - 1) crunch()
    }

    fun crunch() {
        val nextNumber = lastTimeNumberWasCalled[current]?.let { position - it } ?: 0
        lastTimeNumberWasCalled[current] = position
        current = nextNumber
        position++
    }

    fun debug() {
        println("--")
        println("position: $position")
        println("current: $current")
        println(lastTimeNumberWasCalled)
        println("--")
    }
}