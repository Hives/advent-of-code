package days.day22

import lib.Reader
import lib.time

fun main() {
    val myPuzzleInput = Reader("day22.txt").string()
    val pjsPuzzleInput = Reader("day22-pjs-input.txt").string()
    val davidPriorsPuzzleInput = Reader("day22-david-priors-input.txt").string()
    val markFishersPuzzleInput = Reader("day22-mark-fishers-input.txt").string()

    time("part 1 (linked lists)") {
        part1LinkedLists(parseInput(myPuzzleInput))
    }

    time("part 1 (standard lists)") {
        part1StandardLists(parseInput(myPuzzleInput))
    }

    time("part 2, my puzzle input (linked lists)", 2, 0) {
        Part2LinkedLists.run(parseInput(myPuzzleInput))
    }

    listOf(
        Pair("my input (32018)", myPuzzleInput),
        Pair("mark fisher's input (should be 33745)", markFishersPuzzleInput),
        Pair("david prior's input (should be 31956)", davidPriorsPuzzleInput),
        Pair("pj's input (should be 32665)", pjsPuzzleInput),
    ).forEach { (message, input) ->
        time(message, 5, 5) {
            Part2StandardLists(parseInput(input)).run().score
        }
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
