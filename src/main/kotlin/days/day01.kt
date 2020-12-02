package days

import lib.Reader
import lib.combinations

fun main() {
    val input = Reader("day01.txt").listOfInts()

    println("input length = ${input.size}")
    listOf(
        input.combinations(2).first { it.sum() == 2020 },
        input.combinations(3).first { it.sum() == 2020 },
    ).forEach {
        println(it.joinToString(separator = " + ", postfix = " = ") + it.sum())
        println(it.joinToString(separator = " * ", postfix = " = ") + it.reduce { a, b -> a * b })
    }
}
