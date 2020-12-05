import lib.Reader
import lib.findSeat

fun main() {
    Reader("day05.txt").strings().map { findSeat(it).id }.maxOrNull().also { println(it) }
}