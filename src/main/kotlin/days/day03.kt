package days

import lib.Reader
import lib.Toboggan

fun main() {
    val terrain = Reader("day03.txt").strings()

    val slopes = listOf(
        Pair(1, 1),
        Pair(3, 1),
        Pair(5, 1),
        Pair(7, 1),
        Pair(1, 2),
    )

    slopes
        .map { Toboggan(terrain, it.first, it.second).go().trees }
        .onEach { println(it) }
        .reduce { a, b -> a * b }
        .also { println(it) }
}
