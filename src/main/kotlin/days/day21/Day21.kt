package days.day21

import lib.Reader
import lib.time

fun main() {
    val puzzleInput = Reader("day21.txt").strings()
    val exampleInput = example.trim().lines()

    time("part 1") {
        countSafeIngredients(puzzleInput)
    }

    time("part 2") {
        part2(puzzleInput)
    }
}

const val example = "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)\n" +
        "trh fvjkl sbzzf mxmxvkd (contains dairy)\n" +
        "sqjhc fvjkl (contains soy)\n" +
        "sqjhc mxmxvkd sbzzf (contains fish)\n"
