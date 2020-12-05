package lib

import kotlin.math.pow

data class Seat(val row: Int, val col: Int) {
    val id: Int
        get() = 8 * row + col
}

fun findSeat(input: String): Seat {
    val rowString = input.substring(0..6)
    val colString = input.reversed().substring(0..2).reversed()

    return Seat(go(rowString, 0..127, 'F'), go(colString, 0..7, 'L'))
}

private tailrec fun go(input: String, range: IntRange, downChar: Char): Int =
    if (input.length == 0) range.first
    else {
        val nextInput = input.drop(1)
        val sizeOfNextRange = 2.0.pow(nextInput.length).toInt()

        if (input[0] == downChar) {
            val lower = range.first
            val upper = range.first + sizeOfNextRange - 1
            go(nextInput, lower..upper, downChar)
        } else {
            val lower = range.last - sizeOfNextRange + 1
            val upper = range.last
            go(nextInput, lower..upper, downChar)
        }
    }