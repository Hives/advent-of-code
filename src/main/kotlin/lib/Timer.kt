package lib

import kotlin.system.measureNanoTime

fun time(message: String? = null, iterations: Int = 1000, warmUpIterations: Int = 10, f: () -> Any?) {
    println()

    repeat(warmUpIterations) { f() }

    List(iterations) { measureNanoTime { f() } }.average()
        .also { nanoTime ->
            println("${message?.let { "$message\n" } ?: ""}average time over $iterations iterations = ${nanoTime / 1_000_000} milliseconds")
        }

    println("and the answer was: ${f()}")
}