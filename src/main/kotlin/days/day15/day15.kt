package days.day15

val puzzleInput = listOf(9, 6, 0, 10, 18, 2, 1)

fun main() {
    val part1Example = CountingGame(listOf(0, 3, 6))
    part1Example.go(2020)
    // should equal 436
    part1Example.current.also { println(it) }

    val part1 = CountingGame(puzzleInput)
    part1.go(2020)
    // should equal 1238
    part1.current.also { println(it) }
}

class CountingGame(startingNumbers: List<Int>) {
    var history = startingNumbers.toMutableList()
    val lastTimesNumberWasCalled =
        startingNumbers.mapIndexed { index, i -> i to Pair<Int?, Int?>(null, index) }.toMap().toMutableMap()
    var pointer = startingNumbers.size - 1

    val current: Int
        get() = history.last()

    init {
        debug()
    }

    fun go(n: Int) {
        while (pointer < n - 1) crunch()
    }

    fun crunch() {
        val nextNumber = differenceBetweenLastTwoTimes(current)

        pointer++
        history.add(nextNumber)
        lastTimesNumberWasCalled[nextNumber] = Pair(lastTimesNumberWasCalled[nextNumber]?.second, pointer)
    }

    fun debug() {
        println("--")
        println("current:")
        println(current)
        println("lastTimesNumberWasCalled:")
        println(lastTimesNumberWasCalled)
        println("pointer:")
        println(pointer)
        println("--")
    }

    private fun differenceBetweenLastTwoTimes(n: Int): Int {
        val times: Pair<Int?, Int?> = lastTimesNumberWasCalled[n] ?: return 0
        val (lastButOneTime, lastTime) = times

        return if (lastButOneTime == null) 0
        else lastTime!! - lastButOneTime
    }

}