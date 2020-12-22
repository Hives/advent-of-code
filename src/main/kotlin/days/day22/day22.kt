package days.day22

import java.util.*
import lib.Reader
import lib.time

fun main() {
    val puzzleInput = Reader("day22.txt").string()

    time("part 1 (linked lists)") {
        puzzleInput.trim().split("\n\n").map { hand ->
            hand.lines().drop(1).map { it.toInt() }
        }
            .map { LinkedList(it) }
            .let { (handA, handB) ->
                while (!handA.isEmpty() && !handB.isEmpty()) {
                    val a = handA.poll()
                    val b = handB.poll()
                    if (a > b) {
                        handA.add(a)
                        handA.add(b)
                    } else {
                        handB.add(b)
                        handB.add(a)
                    }
                }
                if (handA.isEmpty()) handB else handA
            }
            .score()
    }

    time("part 1 (standard lists)") {
        puzzleInput.trim().split("\n\n").map { hand ->
            hand.lines().drop(1).map { it.toInt() }
        }
            .let { (handA, handB) ->
                play(handA, handB)
            }
            .score()
    }

}

val example = "Player 1:\n" +
        "9\n" +
        "2\n" +
        "6\n" +
        "3\n" +
        "1\n" +
        "\n" +
        "Player 2:\n" +
        "5\n" +
        "8\n" +
        "4\n" +
        "7\n" +
        "10\n"
