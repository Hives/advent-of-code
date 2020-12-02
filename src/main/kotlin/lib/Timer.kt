package lib

import kotlin.system.measureNanoTime

fun time(iterations: Int, warmUpIterations: Int = 10, f: () -> Unit) {
    repeat(warmUpIterations) { f() }

    List(iterations) {
        measureNanoTime(f)
    }.also {
        val averageTimeMillis = it.sum().toDouble() / iterations / 1_000_000
        println("average time over $iterations iterations = $averageTimeMillis milliseconds")
    }
}