package days.day01

import lib.Reader

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
