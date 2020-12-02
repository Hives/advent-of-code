package days

import lib.Reader
import lib.findThreeNumbersThatAddTo
import lib.findTwoNumbersThatAddTo

fun main() {
    val input = Reader("day01.txt").ints()

    listOf(
        input.findTwoNumbersThatAddTo(2020),
        input.findThreeNumbersThatAddTo(2020)
    ).forEach {
        println(it.joinToString(separator = " + ", postfix = " = ") + it.sum())
        println(it.joinToString(separator = " * ", postfix = " = ") + it.reduce { a, b -> a * b })
    }
}
