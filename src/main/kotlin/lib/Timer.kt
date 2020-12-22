package lib

import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.HALF_DOWN
import kotlin.system.measureNanoTime

fun time(message: String? = null, iterations: Int = 1000, warmUpIterations: Int = 10, f: () -> Any?) {
    println()
    message?.also { println(it) }

    repeat(warmUpIterations) { f() }

    lateinit var answer: Any

    List(iterations) { measureNanoTime { answer = f() ?: "no answer" } }
        .also { times ->
            val average = times.average()

            val time = if (average > 1_000_000_000) "${round(average / 1_000_000_000)} seconds"
            else if (average > 1_000_000) "${round(average / 1_000_000)} milliseconds"
            else "${round(average / 1_000)} microseconds"

            println("average time over $iterations iterations = $time")
        }

    println("and the answer was: $answer")
}

private fun round(number: Double) = BigDecimal(number).setScale(2, HALF_DOWN)
