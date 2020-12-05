package days.day05

import kotlin.math.pow

fun findSeatUsingBinaryStrings(input: String): Int {
    val row = input.substring(0..6).replace('F','0').replace('B', '1')
    val col = input.substring(7..9).replace('L','0').replace('R', '1')
    return Integer.parseInt("$row$col", 2)
}

// no longer used, preserved for posterity 😞
private fun String.binaryToInt(): Int =
    this.reversed().mapIndexed { index, c -> c.toString().toInt() * 2.0.pow(index).toInt() }.sum()
