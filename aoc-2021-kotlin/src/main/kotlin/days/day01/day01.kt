package days.day01

import lib.Reader

fun main() {
    val input = Reader("day01.txt").ints()

    input.zipWithNext().fold(0) { acc, (prev, next) ->
        if (next > prev) acc + 1 else acc
    }.also { println(it) }

    input.windowed(3).zipWithNext().fold(0) { acc, (prev, next) ->
        if (next.sum() > prev.sum()) acc + 1 else acc
    }.also { println(it) }
}
