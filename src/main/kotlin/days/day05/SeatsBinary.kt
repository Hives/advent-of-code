package days.day05

import kotlin.math.pow

fun findSeatUsingBinaryStrings(input: String): Int {
    val row = input.substring(0..6).map { if (it == 'F') '0' else '1' }.joinToString("")
    val col = input.substring(7..9).map { if (it == 'L') '0' else '1' }.joinToString("")
    return "$row$col".binaryToInt()
}

private fun String.binaryToInt(): Int =
    this.reversed().mapIndexed { index, c -> c.toString().toInt() * 2.0.pow(index).toInt() }.sum()

fun main() {
    listOf(
        "00001",
        "00010",
        "00011",
        "00100",
        "00101",
        "00110",
    ).forEach { println(it.binaryToInt()) }
}
