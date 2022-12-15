package lib

import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import kotlin.system.measureNanoTime

fun <T> time(iterations: Int = 100, warmUpIterations: Int = 15, message: String? = null, f: () -> T?): T? {
    println()
    if (!message.isNullOrEmpty()) println(message)

    var answer: T? = null

    repeat(warmUpIterations) { f() }

    List(iterations) { measureNanoTime { answer = f() } }
        .also { times ->
            val average = times.average()

            val time = when {
                average > 1_000_000_000 -> "${round(average / 1_000_000_000)} seconds"
                average > 1_000_000 -> "${round(average / 1_000_000)} milliseconds"
                else -> "${round(average / 1_000)} microseconds"
            }

            println("average time over $iterations iterations = $time")
        }

    return answer
}

private fun round(number: Double) = BigDecimal(number).setScale(2, HALF_DOWN)
