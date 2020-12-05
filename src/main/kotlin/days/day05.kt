import lib.Reader
import lib.Seat
import lib.findSeat

fun main() {
    val seatIds = Reader("day05.txt").strings().map { findSeat(it) }.map { it.id }

    seatIds.maxOrNull()
        .also { println("part 1: $it") }

    val allPossibleSeats = List(128) { it }.flatMap { row ->
        List(8) { it }.map { col ->
            Seat(row, col)
        }
    }
    val allPossibleSeatIds = allPossibleSeats.map { it.id }
    val missingSeatIds = allPossibleSeatIds - seatIds

    missingSeatIds.singleOrNull { !missingSeatIds.contains(it + 1) && !missingSeatIds.contains(it - 1) }
        .also { println("part 2: $it") }
}