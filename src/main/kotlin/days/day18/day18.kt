package days.day18

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day18.txt").strings().map { splitString(it) }
    time("part 1") {
        input
            .map { parseSymbols(it).evaluate().value }
            .reduce { a, b -> a + b }
    }

    // average time was 1.5ms
    time("part 2") {
        input
            .map { parsy(it).value }
            .reduce { a, b -> a + b }
    }

}