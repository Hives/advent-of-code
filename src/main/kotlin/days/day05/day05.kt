package days.day05

import lib.Reader

fun main() {
    val input = Reader("day05.txt").strings()

    listOf(
        Pair("recursive split method", ::findSeatUsingRecursiveSplit),
        Pair("binary string method", ::findSeatUsingBinaryStrings)
    ).forEach { (description, findSeat) ->
        val seatIds = input.map { findSeat(it) }

        seatIds.maxOrNull()
            .also { println("part 1, $description: $it") }

        val allPossibleSeatIds = List(128) { it }.flatMap { row ->
            List(8) { it }.map { col ->
                calculateSeatId(row, col)
            }
        }
        val missingSeatIds = allPossibleSeatIds - seatIds

        missingSeatIds.singleOrNull { !missingSeatIds.contains(it + 1) && !missingSeatIds.contains(it - 1) }
            .also { println("part 2, $description: $it") }
    }
}