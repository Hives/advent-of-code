package days.day17

import lib.Reader
import lib.time

val example = ".#.\n" +
        "..#\n" +
        "###"

fun main() {
    val puzzleInput = Reader("day17.txt").string()

    time("part 1", 100, 10) {
        val c = ConwayCubes3D(puzzleInput)
        c.run(6)
        c.countActive()
    }

    time("part 2", 100, 10) {
        val c = ConwayCubes4D(puzzleInput)
        c.run(6)
        c.countActive()
    }
}
