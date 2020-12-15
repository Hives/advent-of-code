package days.day15

import lib.time

val puzzleInput = listOf(9, 6, 0, 10, 18, 2, 1)

fun main() {

    time ("part 1") {
        val part1 = CountingGame(puzzleInput)
        part1.go(2020)
        // should equal 1238
        part1.current
    }

    time ("part 2", 1, 0) {
        val part2 = CountingGame(puzzleInput)
        part2.go(30000000)
        // should equal 3745954
        part2.current
    }

}

class CountingGame(startingNumbers: List<Int>) {
    private var history = startingNumbers.toMutableList()
    private val lastTimesNumberWasCalled =
        startingNumbers.mapIndexed { index, i -> i to Pair<Int?, Int?>(null, index) }.toMap().toMutableMap()
    private var pointer = startingNumbers.size - 1

    val current: Int
        get() = history.last()

    fun go(n: Int) {
        while (pointer < n - 1) crunch()
    }

    private fun crunch() {
        val nextNumber = differenceBetweenLastTwoTimes(current)

        pointer++
        history.add(nextNumber)
        lastTimesNumberWasCalled[nextNumber] = Pair(lastTimesNumberWasCalled[nextNumber]?.second, pointer)
    }

    private fun differenceBetweenLastTwoTimes(n: Int): Int {
        val times: Pair<Int?, Int?> = lastTimesNumberWasCalled[n] ?: return 0
        val (lastButOneTime, lastTime) = times

        return if (lastButOneTime == null) 0
        else lastTime!! - lastButOneTime
    }

}