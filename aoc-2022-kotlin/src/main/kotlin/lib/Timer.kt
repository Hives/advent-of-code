package lib

import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import kotlin.system.measureNanoTime

const val ONE_MINUTE = 60_000_000_000
const val ONE_SECOND = 1_000_000_000
const val ONE_MILLISECOND = 1_000_000

fun <T> time(iterations: Int = 100, warmUpIterations: Int = 15, message: String? = null, f: () -> T?): T? {
    println()
    if (!message.isNullOrEmpty()) println(message)

    var answer: T? = null

    repeat(warmUpIterations) { f() }

    List(iterations) { measureNanoTime { answer = f() } }
        .also { times ->
            val average = times.average()

            val time = when {
                average > ONE_MINUTE -> "${round(average / ONE_MINUTE)} minutes"
                average > ONE_SECOND -> "${round(average / ONE_SECOND)} seconds"
                average > ONE_MILLISECOND -> "${round(average / ONE_MILLISECOND)} milliseconds"
                else -> "${round(average / 1_000)} microseconds"
            }

            println("average time over $iterations iteration(s) = $time")
        }

    return answer
}

private fun round(number: Double) = BigDecimal(number).setScale(2, HALF_DOWN)
