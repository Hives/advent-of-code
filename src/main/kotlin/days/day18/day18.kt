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

    input.map { (parsy(it).single() as Value).value }
        .reduce { a, b -> a + b }
        .also { println(it) }

}