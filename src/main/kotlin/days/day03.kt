package days

import lib.Reader
import lib.Slope
import lib.Toboggan
import lib.time

fun main() {
    val terrain = Reader("day03.txt").strings()

    val slopes = listOf(
            Slope(1, 1),
            Slope(3, 1),
            Slope(5, 1),
            Slope(7, 1),
            Slope(1, 2),
    )

    slopes
            .map { slope -> Toboggan(terrain, slope).go().trees }
            .onEach { println(it) }
            .reduce { a, b -> a * b }
            .also { println(it) }
}
