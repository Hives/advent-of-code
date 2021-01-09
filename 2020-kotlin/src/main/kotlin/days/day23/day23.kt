package days.day23

import lib.time

fun main() {
    time("part 1 (should be 72496583)") {
        val game = CrabCups(puzzleInput)
        game.run(100)
        game.getCups().drop(1).joinToString("") { it.toString() }
    }

    time("part 2 (should be 41785843847)", 3, 2) {
        val game = CrabCups(puzzleInput, 1_000_000)
        game.run(10_000_000)
        game.take(3).let { it[1].toLong() * it[2].toLong() }
    }

    time("part 2, pjs input (should be 111057672960)", 3, 2) {
        val game = CrabCups(pjsInput, 1_000_000)
        game.run(10_000_000)
        game.take(3).let { it[1].toLong() * it[2].toLong() }
    }
}

val example = "389125467"
val puzzleInput = "315679824"
val pjsInput = "157623984"
