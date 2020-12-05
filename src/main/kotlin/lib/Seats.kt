package lib

data class Seat(val row: Int, val col: Int) {
    val id: Int
        get() = 8 * row + col
}

fun findSeat(input: String): Seat =
    Seat(
        binarySearch(input.substring(0..6), 0..127),
        binarySearch(input.substring(7..9), 0..7)
    )

private tailrec fun binarySearch(input: String, range: IntRange): Int =
    if (input.length == 0) range.first
    else {
        val sizeOfNextRange = (range.last - range.first + 1) / 2

        val nextRange = if (input[0] == 'F' || input[0] == 'L') {
            val lower = range.first
            val upper = range.first + sizeOfNextRange - 1
            lower..upper
        } else {
            val lower = range.last - sizeOfNextRange + 1
            val upper = range.last
            lower..upper
        }

        binarySearch(input.drop(1), nextRange)
    }