package lib

import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import kotlin.system.measureNanoTime

const val ONE_MINUTE = 60_000_000_000
const val ONE_SECOND = 1_000_000_000
const val ONE_MILLISECOND = 1_000_000

fun <T> time(iterations: Int = 50, warmUp: Int = 50, message: String? = null, f: () -> T?): T? {
    println()
    if (!message.isNullOrEmpty()) println(message)

    var answer: T? = null

    repeat(warmUp) { f() }

    val times = List(iterations) { measureNanoTime { answer = f() } }

    println("average time over $iterations iteration(s) = ${formatTime(times.average())}")

    return answer
}

private fun formatTime(time: Double): String = when {
    time > ONE_MINUTE -> "${round(time / ONE_MINUTE)} minutes"
    time > ONE_SECOND -> "${round(time / ONE_SECOND)} seconds"
    time > ONE_MILLISECOND -> "${round(time / ONE_MILLISECOND)} milliseconds"
    else -> "${round(time / 1_000)} microseconds"
}

private fun round(number: Double) = BigDecimal(number).setScale(2, HALF_DOWN)
