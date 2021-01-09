package days.day05

fun findSeatUsingRecursiveSplit(input: String): Int {
    val row = recursiveSplit(input.substring(0..6), 0..127)
    val col = recursiveSplit(input.substring(7..9), 0..7)
    return calculateSeatId(row, col)
}

fun calculateSeatId(row: Int, col: Int) = 8 * row + col

private tailrec fun recursiveSplit(input: String, range: IntRange): Int =
    if (input.isEmpty()) range.first
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

        recursiveSplit(input.drop(1), nextRange)
    }
