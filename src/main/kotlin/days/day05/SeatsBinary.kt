package days.day05

fun findSeatUsingBinaryStrings(input: String): Int =
    input
        .replace('F', '0')
        .replace('B', '1')
        .replace('L', '0')
        .replace('R', '1')
        .let { it.toInt(2) }
