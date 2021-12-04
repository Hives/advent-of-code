package lib

import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import kotlin.system.measureNanoTime

fun time(iterations: Int = 100, warmUpIterations: Int = 15, message: String? = null, f: () -> Any?): Any? {
    println()
    if (!message.isNullOrEmpty()) println(message)

    repeat(warmUpIterations) { f() }

    List(iterations) { measureNanoTime { f() } }
        .also { times ->
            val average = times.average()

            val time = when {
                average > 1_000_000_000 -> "${round(average / 1_000_000_000)} seconds"
                average > 1_000_000 -> "${round(average / 1_000_000)} milliseconds"
                else -> "${round(average / 1_000)} microseconds"
            }

            println("average time over $iterations iterations = $time")
        }

    return f()
}

private fun round(number: Double) = BigDecimal(number).setScale(2, HALF_DOWN)
