package days.day18

import lib.Reader
import lib.time

fun main() {
    val input = Reader("day18.txt").strings().map { stringToSymbols(it) }

    time("part 1") {
        input
            .map { evaluateSymbols1(it).value }
            .reduce { a, b -> a + b }
    }

    // average time was 1.5ms
    time("part 2") {
        input
            .map { evaluateSymbols2(it).value }
            .reduce { a, b -> a + b }
    }

}