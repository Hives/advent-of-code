package days.day22

import lib.Reader
import lib.time

fun main() {
    val myPuzzleInput = Reader("day22.txt").string()
    val pjsPuzzleInput = Reader("day22-pjs-input.txt").string()
    val davidPriorsPuzzleInput = Reader("day22-david-priors-input.txt").string()
    val markFishersPuzzleInput = Reader("day22-mark-fishers-input.txt").string()
    val kevinHannasPuzzleInput = Reader("day22-kevin-hannas-input.txt").string()

    time("part 1 (linked lists)") {
        part1LinkedLists(parseInput(myPuzzleInput))
    }

    time("part 1 (standard lists)") {
        part1StandardLists(parseInput(myPuzzleInput))
    }

    listOf(
        Pair("part 2, standard lists, my input (should be 32018)", myPuzzleInput),
        Pair("part 2, standard lists, mark fisher's input (should be 33745)", markFishersPuzzleInput),
        Pair("part 2, standard lists, david prior's input (should be 31956)", davidPriorsPuzzleInput),
        Pair("part 2, standard lists, pj's input (should be 32665)", pjsPuzzleInput),
        Pair("part 2, standard lists, kevin hanna's input (should be 32054)", kevinHannasPuzzleInput)
    ).forEach { (message, input) ->
        time(message, 5, 2) {
            Part2StandardLists.run(parseInput(input))
        }
    }

    listOf(
        Pair("part 2, linked lists, my input (should be 32018)", myPuzzleInput),
        Pair("part 2, linked lists, mark fisher's input (should be 33745)", markFishersPuzzleInput),
        Pair("part 2, linked lists, david prior's input (should be 31956)", davidPriorsPuzzleInput),
        Pair("part 2, linked lists, pj's input (should be 32665)", pjsPuzzleInput),
        Pair("part 2, linked lists, kevin hanna's input (should be 32054)", kevinHannasPuzzleInput)
    ).forEach { (message, input) ->
        time(message, 5, 2) {
            Part2LinkedLists.run(parseInput(input))
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
